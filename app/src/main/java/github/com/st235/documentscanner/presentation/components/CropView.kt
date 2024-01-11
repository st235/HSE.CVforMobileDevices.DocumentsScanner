package github.com.st235.documentscanner.presentation.components

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.Px
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.minus
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Returns image to canvas ratio.
 * To get new image sizes, one needs to divide the
 * real image dimension to this ratio.
 *
 * newWidth = imageWidth / scaleFactor
 * newHeight = imageHeight / scaleFactor
 */
private fun calculateScaleToFitFactor(
    imageWidth: Float, imageHeight: Float,
    canvasWidth: Float, canvasHeight: Float
): Float {
    val factor = imageWidth / canvasWidth

    if (imageHeight / factor <= canvasHeight) {
        return factor
    }

    return imageHeight / canvasHeight
}

private fun dot(a: Offset, b: Offset): Float {
    return a.x * b.x + a.y * b.y
}

// Compute barycentric coordinates (u, v, w) for
// point p with respect to triangle (a, b, c)
private fun toBarycentric(p: Offset, a: Offset, b: Offset, c: Offset): FloatArray {
    val v0 = b.minus(a)
    val v1 = c.minus(a)
    val v2 = p.minus(a)

    val d00 = dot(v0, v0)
    val d01 = dot(v0, v1)
    val d11 = dot(v1, v1)
    val d20 = dot(v2, v0)
    val d21 = dot(v2, v1)
    val denominator = d00 * d11 - d01 * d01

    val v = (d11 * d20 - d01 * d21) / denominator
    val w = (d00 * d21 - d01 * d20) / denominator
    val u = 1.0f - v - w

    return floatArrayOf(u, w, v)
}

fun checkIfConvex(p: Offset, a: Offset, b: Offset, c: Offset): Boolean {
    // Check whether ab and bc are collinear and makes degenerate triangle.
    val ab = b.minus(a)
    val bc = c.minus(b)

    // Simplified cross-product of a 2d vector,
    // AxB = (AyBz − AzBy, AzBx − AxBz, AxBy − AyBx).
    if ((ab.x * bc.y - ab.y * bc.x) < 0.0001f) {
        return false
    }

    val barycentric = toBarycentric(p, a, b, c)
    val alpha = barycentric[0]
    val beta = barycentric[1]
    val gamma = barycentric[2]

    return (alpha < 0 && beta > 0 && gamma > 0) ||
        (alpha > 0 && beta < 0 && gamma > 0) ||
        (alpha > 0 && beta > 0 && gamma < 0)
}

data class CroppingViewport(
    val leftTop: Offset,
    val leftBottom: Offset,
    val rightTop: Offset,
    val rightBottom: Offset,
) {
    companion object {
        fun fromRawCoordinates(
            ltx: Float, lty: Float,
            lbx: Float, lby: Float,
            rtx: Float, rty: Float,
            rbx: Float, rby: Float
        ): CroppingViewport {
            return CroppingViewport(
                leftTop = Offset(ltx, lty),
                leftBottom = Offset(lbx, lby),
                rightTop = Offset(rtx, rty),
                rightBottom = Offset(rbx, rby),
            )
        }
    }
}

private val CroppingViewport.left: Float
    get() {
        return min(leftTop.x, leftBottom.x)
    }

private val CroppingViewport.right: Float
    get() {
        return max(rightTop.x, rightBottom.x)
    }

private val CroppingViewport.top: Float
    get() {
        return min(leftTop.y, rightTop.y)
    }

private val CroppingViewport.bottom: Float
    get() {
        return max(leftBottom.y, rightBottom.y)
    }

private fun CroppingViewport.toCanvasViewport(scaleFactor: Float): CroppingViewport {
    return CroppingViewport(
        leftTop = leftTop.div(scaleFactor),
        leftBottom = leftBottom.div(scaleFactor),
        rightTop = rightTop.div(scaleFactor),
        rightBottom = rightBottom.div(scaleFactor)
    )
}

private fun CroppingViewport.toOriginalImageViewport(scaleFactor: Float): CroppingViewport {
    return CroppingViewport(
        leftTop = leftTop.times(scaleFactor),
        leftBottom = leftBottom.times(scaleFactor),
        rightTop = rightTop.times(scaleFactor),
        rightBottom = rightBottom.times(scaleFactor)
    )
}

class CropViewDragController(
    initialCroppingViewport: CroppingViewport,
    @Px private val cornerTouchRadius: Float,
) {
    private companion object {
        fun squareDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return (x1 - x2).pow(2) + (y1 - y2).pow(2)
        }
    }

    enum class CornerDraggingState {
        IDLING,
        DRAGGING_LEFT_TOP,
        DRAGGING_LEFT_BOTTOM,
        DRAGGING_RIGHT_TOP,
        DRAGGING_RIGHT_BOTTOM,
    }

    var viewport by mutableStateOf(initialCroppingViewport)
        private set

//    val imageViewport: CroppingViewport
//        get() {
//            return
//        }

    private var viewBounds by mutableStateOf(IntSize.Zero)

    private var draggingCornerState by mutableStateOf(CornerDraggingState.IDLING)

    private var imageBoundsWithinView = Rect.Zero

    private var imageScaleFactor: Float = 1f

    fun onViewBoundsChanged(newBounds: IntSize) {
//        viewBounds = newBounds
    }

    fun onImageBoundsWithinViewChanged(
        newBounds: Rect,
        scaleFactor: Float
    ) {
        val lt = viewport.leftTop
        val lb = viewport.leftBottom
        val rt = viewport.rightTop
        val rb = viewport.rightBottom

        val oldOffset = Offset(imageBoundsWithinView.left, imageBoundsWithinView.top)
        val offset = Offset(newBounds.left, newBounds.top)

        viewport = viewport.copy(
            leftTop = lt.minus(oldOffset).plus(offset),
            leftBottom = lb.minus(oldOffset).plus(offset),
            rightTop = rt.minus(oldOffset).plus(offset),
            rightBottom = rb.minus(oldOffset).plus(offset),
        )

        imageBoundsWithinView = newBounds
        imageScaleFactor = scaleFactor
    }

    fun onStartDragging(x: Float, y: Float): Boolean {
        draggingCornerState = obtainDraggingState(x, y)
        return draggingCornerState != CornerDraggingState.IDLING
    }

    fun onDragging(x: Float, y: Float): Boolean {
        if (draggingCornerState == CornerDraggingState.IDLING) {
            return false
        }

        val newViewport = when (draggingCornerState) {
            CornerDraggingState.DRAGGING_LEFT_TOP -> viewport.copy(leftTop = dragLeftTopCornerTo(x, y))
            CornerDraggingState.DRAGGING_LEFT_BOTTOM -> viewport.copy(leftBottom = dragLeftBottomCornerTo(x, y))
            CornerDraggingState.DRAGGING_RIGHT_TOP -> viewport.copy(rightTop = dragRightTopCornerTo(x, y))
            CornerDraggingState.DRAGGING_RIGHT_BOTTOM -> viewport.copy(rightBottom = dragRightBottomCornerTo(x, y))
            CornerDraggingState.IDLING -> throw IllegalArgumentException("Cannot create new viewport as no dragging is happening")
        }

        if (!checkIfConvex(newViewport.leftTop, newViewport.leftBottom, newViewport.rightTop, newViewport.rightBottom)) {
            return false
        }

        viewport = newViewport
        return true
    }

    fun onStopDragging() {
        draggingCornerState = CornerDraggingState.IDLING
    }

    private fun dragLeftTopCornerTo(x: Float, y: Float): Offset {
        // Original image bounds.
        var newX = max(imageBoundsWithinView.left, min(imageBoundsWithinView.right, x))
        var newY = max(imageBoundsWithinView.top, min(imageBoundsWithinView.bottom, y))

        newX = min(newX, viewport.right - cornerTouchRadius)
        newY = min(newY, viewport.bottom - cornerTouchRadius)

        return Offset(newX, newY)
    }

    private fun dragRightTopCornerTo(x: Float, y: Float): Offset {
        // Original image bounds.
        var newX = max(imageBoundsWithinView.left, min(imageBoundsWithinView.right, x))
        var newY = max(imageBoundsWithinView.top, min(imageBoundsWithinView.bottom, y))

        newX = max(newX, viewport.left + cornerTouchRadius)
        newY = min(newY, viewport.bottom - cornerTouchRadius)

        return Offset(newX, newY)
    }

    private fun dragLeftBottomCornerTo(x: Float, y: Float): Offset {
        // Original image bounds.
        var newX = max(imageBoundsWithinView.left, min(imageBoundsWithinView.right, x))
        var newY = max(imageBoundsWithinView.top, min(imageBoundsWithinView.bottom, y))

        newX = min(newX, viewport.right - cornerTouchRadius)
        newY = max(newY, viewport.top + cornerTouchRadius)

        return Offset(newX, newY)
    }

    private fun dragRightBottomCornerTo(x: Float, y: Float): Offset {
        // Original image bounds.
        var newX = max(imageBoundsWithinView.left, min(imageBoundsWithinView.right, x))
        var newY = max(imageBoundsWithinView.top, min(imageBoundsWithinView.bottom, y))

        newX = max(newX, viewport.left + cornerTouchRadius)
        newY = max(newY, viewport.top + cornerTouchRadius)

        return Offset(newX, newY)
    }

    private fun obtainDraggingState(x: Float, y: Float): CornerDraggingState {
        val lt = viewport.leftTop
        val lb = viewport.leftBottom
        val rt = viewport.rightTop
        val rb = viewport.rightBottom

        return when {
            lt.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_LEFT_TOP
            lb.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_LEFT_BOTTOM
            rt.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_RIGHT_TOP
            rb.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_RIGHT_BOTTOM
            else -> CornerDraggingState.IDLING
        }
    }

    private fun Offset.withinTouchRadius(x: Float, y: Float): Boolean {
        val squareDistance = squareDistance(this.x, this.y, x, y)
        return squareDistance <= cornerTouchRadius.pow(2)
    }
}

@Composable
fun CropView(
    image: Bitmap,
    croppingViewport: CroppingViewport,
    modifier: Modifier = Modifier,
    overlayColor: Color = Color(0x882e2e2e),
    cornerColor: Color = Color.White,
    cornerRadius: Dp = 8f.dp,
    cornerTouchRadius: Dp = 32f.dp,
    onCornersChanged: (CroppingViewport) -> Unit = {}
) {
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
    val cornerTouchRadiusPx = with(LocalDensity.current) { cornerTouchRadius.toPx() }

    val dragController by remember {
        mutableStateOf(
            CropViewDragController(
                croppingViewport,
                cornerTouchRadiusPx,
            )
        )
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { dragController.onViewBoundsChanged(it) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { point ->
                        dragController.onStartDragging(point.x, point.y)
                    },
                    onDrag = { change, _ ->
                        val point = change.position

                        if (dragController.onDragging(point.x, point.y)) {
                            change.consume()
                        }
                    },
                    onDragEnd = {
                        dragController.onStopDragging()
                    },
                    onDragCancel = {
                        dragController.onStopDragging()
                    }
                )
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val imageWidth = image.width.toFloat()
        val imageHeight = image.height.toFloat()

        val scaleFactor = calculateScaleToFitFactor(imageWidth, imageHeight, canvasWidth, canvasHeight)

        val newWidth = imageWidth / scaleFactor
        val newHeight = imageHeight / scaleFactor

        val previewBitmap = Bitmap.createScaledBitmap(
            image,
            newWidth.toInt(),
            newHeight.toInt(),
            false
        )

        val offsetX = (canvasWidth - previewBitmap.width) / 2
        val offsetY = (canvasHeight - previewBitmap.height) / 2

        dragController.onImageBoundsWithinViewChanged(
            newBounds = Rect(Offset(offsetX, offsetY), Size(newWidth, newHeight)),
            scaleFactor = scaleFactor
        )

        drawImage(
            previewBitmap.asImageBitmap(),
            topLeft = Offset(
                offsetX,
                offsetY,
            )
        )

        val viewport = dragController.viewport

        val path = Path().apply {
            // CW add view bounds.
            moveTo(0f, 0f)
            relativeLineTo(canvasWidth, 0f)
            relativeLineTo(0f, canvasHeight)
            relativeLineTo(-canvasWidth, 0f)
            relativeLineTo(0f, -canvasHeight)

            // CCW add crop area bounds.
            moveTo(viewport.leftTop.x, viewport.leftTop.y)
            lineTo(viewport.leftBottom.x, viewport.leftBottom.y)
            lineTo(viewport.rightBottom.x, viewport.rightBottom.y)
            lineTo(viewport.rightTop.x, viewport.rightTop.y)
            lineTo(viewport.leftTop.x, viewport.leftTop.y)
        }

        drawPath(
            path,
            color = overlayColor
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.leftTop.x, viewport.leftTop.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.leftBottom.x, viewport.leftBottom.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.rightBottom.x, viewport.rightBottom.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.rightTop.x, viewport.rightTop.y),
        )
    }
}
