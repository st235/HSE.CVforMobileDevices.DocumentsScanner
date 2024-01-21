package github.com.st235.documentscanner.presentation.widgets

import android.graphics.Bitmap
import androidx.annotation.Px
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Composable
fun CropView(
    image: Bitmap,
    modifier: Modifier = Modifier,
    imageCroppedArea: CropArea = CropArea.ALL,
    overlayColor: Color = Color(0x882e2e2e),
    cornerColor: Color = Color.White,
    cornerRadius: Dp = 8f.dp,
    cornerTouchRadius: Dp = 32f.dp,
    onCropAreaChanged: (CropArea) -> Unit = {}
) {
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
    val cornerTouchRadiusPx = with(LocalDensity.current) { cornerTouchRadius.toPx() }

    val dragController by remember { mutableStateOf(CropViewDragController()) }

    LaunchedEffect(true) {
        onCropAreaChanged(imageCroppedArea)
    }

    val isConvexHull = checkIfBarycentricCoordinatesValid(
        p = imageCroppedArea.topLeft,
        a = imageCroppedArea.bottomRight, b = imageCroppedArea.topRight, c = imageCroppedArea.bottomLeft
    ) { a, b, c ->
        a < 0 && b > 0 && c > 0
    }

    dragController.imageCropArea = if (!isConvexHull || imageCroppedArea == CropArea.ALL) {
        CropArea(
            topLeft = Offset(0f, 0f),
            topRight = Offset(image.width.toFloat(), 0f),
            bottomRight = Offset(image.width.toFloat(), image.height.toFloat()),
            bottomLeft = Offset(0f, image.height.toFloat()),
        )
    } else {
        imageCroppedArea
    }
    dragController.cornerTouchRadius = cornerTouchRadiusPx

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { point ->
                        dragController.onStartDragging(point.x, point.y)
                    },
                    onDrag = { change, _ ->
                        val point = change.position

                        if (dragController.onDragging(point.x, point.y)) {
                            change.consume()
                            onCropAreaChanged(dragController.imageCropArea)
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

        val viewport = dragController.canvasCropArea

        val path = Path().apply {
            // CW add view bounds.
            moveTo(0f, 0f)
            relativeLineTo(canvasWidth, 0f)
            relativeLineTo(0f, canvasHeight)
            relativeLineTo(-canvasWidth, 0f)
            relativeLineTo(0f, -canvasHeight)

            // CCW add crop area bounds.
            moveTo(viewport.topLeft.x, viewport.topLeft.y)
            lineTo(viewport.bottomLeft.x, viewport.bottomLeft.y)
            lineTo(viewport.bottomRight.x, viewport.bottomRight.y)
            lineTo(viewport.topRight.x, viewport.topRight.y)
            lineTo(viewport.topLeft.x, viewport.topLeft.y)
        }

        drawPath(
            path,
            color = overlayColor
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.topLeft.x, viewport.topLeft.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.bottomLeft.x, viewport.bottomLeft.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.bottomRight.x, viewport.bottomRight.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.topRight.x, viewport.topRight.y),
        )
    }
}

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

data class CropArea(
    val topLeft: Offset,
    val bottomLeft: Offset,
    val topRight: Offset,
    val bottomRight: Offset,
) {
    companion object {
        val ALL = CropArea(
            topLeft = Offset.Zero,
            bottomLeft = Offset.Zero,
            topRight = Offset.Zero,
            bottomRight = Offset.Zero,
        )
    }
}

private fun CropArea.toCanvasCoordinates(scaleFactor: Float): CropArea {
    return CropArea(
        topLeft = topLeft.div(scaleFactor),
        bottomLeft = bottomLeft.div(scaleFactor),
        topRight = topRight.div(scaleFactor),
        bottomRight = bottomRight.div(scaleFactor)
    )
}

private fun CropArea.toImageCoordinates(scaleFactor: Float): CropArea {
    return CropArea(
        topLeft = topLeft.times(scaleFactor),
        bottomLeft = bottomLeft.times(scaleFactor),
        topRight = topRight.times(scaleFactor),
        bottomRight = bottomRight.times(scaleFactor)
    )
}

class CropViewDragController {
    private companion object {
        fun squareDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return (x1 - x2).pow(2) + (y1 - y2).pow(2)
        }
    }

    enum class CornerDraggingState {
        IDLING,
        DRAGGING_TOP_LEFT,
        DRAGGING_BOTTOM_LEFT,
        DRAGGING_TOP_RIGHT,
        DRAGGING_BOTTOM_RIGHT,
    }

    private var draggingCornerState by mutableStateOf(CornerDraggingState.IDLING)

    private var imageBoundsWithinView = Rect.Zero

    private var imageScaleFactor: Float = 1f

    var imageCropArea by mutableStateOf(CropArea.ALL)

    @Px var cornerTouchRadius: Float = 0F

    val canvasCropArea: CropArea
        get() {
            val canvasCropArea = imageCropArea.toCanvasCoordinates(imageScaleFactor)
            return CropArea(
                topLeft = canvasCropArea.topLeft.plus(imageBoundsWithinView.topLeft),
                topRight = canvasCropArea.topRight.plus(imageBoundsWithinView.topLeft),
                bottomRight = canvasCropArea.bottomRight.plus(imageBoundsWithinView.topLeft),
                bottomLeft = canvasCropArea.bottomLeft.plus(imageBoundsWithinView.topLeft),
            )
        }

    fun onImageBoundsWithinViewChanged(
        newBounds: Rect,
        scaleFactor: Float
    ) {
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

        val newCanvasCropArea = when (draggingCornerState) {
            CornerDraggingState.DRAGGING_TOP_LEFT -> canvasCropArea.copy(topLeft = dragPoint(x, y))
            CornerDraggingState.DRAGGING_TOP_RIGHT -> canvasCropArea.copy(topRight = dragPoint(x, y))
            CornerDraggingState.DRAGGING_BOTTOM_LEFT -> canvasCropArea.copy(bottomLeft = dragPoint(x, y))
            CornerDraggingState.DRAGGING_BOTTOM_RIGHT -> canvasCropArea.copy(bottomRight = dragPoint(x, y))
            CornerDraggingState.IDLING -> throw IllegalArgumentException("Cannot create new viewport as no dragging is happening")
        }

        val checkIfCroppingAreaIsAConvexHull = checkIfBarycentricCoordinatesValid(
            p = newCanvasCropArea.topLeft,
            a = newCanvasCropArea.bottomRight, b = newCanvasCropArea.topRight, c = newCanvasCropArea.bottomLeft
        ) { a, b, c ->
            a < 0 && b > 0 && c > 0
        }

        if (checkIfCroppingAreaIsAConvexHull) {
            imageCropArea = CropArea(
                topLeft = newCanvasCropArea.topLeft.minus(imageBoundsWithinView.topLeft),
                topRight = newCanvasCropArea.topRight.minus(imageBoundsWithinView.topLeft),
                bottomRight = newCanvasCropArea.bottomRight.minus(imageBoundsWithinView.topLeft),
                bottomLeft = newCanvasCropArea.bottomLeft.minus(imageBoundsWithinView.topLeft),
            ).toImageCoordinates(imageScaleFactor)
        }
        return true
    }

    fun onStopDragging() {
        draggingCornerState = CornerDraggingState.IDLING
    }

    private fun dragPoint(x: Float, y: Float): Offset {
        // Original image bounds.
        val newX = max(imageBoundsWithinView.left, min(imageBoundsWithinView.right, x))
        val newY = max(imageBoundsWithinView.top, min(imageBoundsWithinView.bottom, y))
        return Offset(newX, newY)
    }

    private fun obtainDraggingState(x: Float, y: Float): CornerDraggingState {
        val cropArea = canvasCropArea
        val lt = cropArea.topLeft
        val lb = cropArea.bottomLeft
        val rt = cropArea.topRight
        val rb = cropArea.bottomRight

        return when {
            lt.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_TOP_LEFT
            lb.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_BOTTOM_LEFT
            rt.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_TOP_RIGHT
            rb.withinTouchRadius(x, y) -> CornerDraggingState.DRAGGING_BOTTOM_RIGHT
            else -> CornerDraggingState.IDLING
        }
    }

    private fun Offset.withinTouchRadius(x: Float, y: Float): Boolean {
        val squareDistance = squareDistance(this.x, this.y, x, y)
        return squareDistance <= cornerTouchRadius.pow(2)
    }
}

private inline fun checkIfBarycentricCoordinatesValid(
    p: Offset,
    a: Offset, b: Offset, c: Offset,
    predicate: (a: Float, b: Float, c: Float) -> Boolean
): Boolean {
    // Check whether ab and bc are collinear and makes degenerate triangle.
    val ab = b.minus(a)
    val bc = c.minus(b)

    // Simplified cross-product of a 2d vector,
    // AxB = (AyBz − AzBy, AzBx − AxBz, AxBy − AyBx).
    if (abs(ab.x * bc.y - ab.y * bc.x) < 0.0001f) {
        return false
    }

    val barycentric = toBarycentric(p, a, b, c)
    val alpha = barycentric[0]
    val beta = barycentric[1]
    val gamma = barycentric[2]

    return predicate(alpha, beta, gamma)
}

/**
 * Calculates dot-product of a vector denoted by two points A and B.
 */
private fun dot(a: Offset, b: Offset): Float {
    return a.x * b.x + a.y * b.y
}

/**
 * Computes barycentric coordinates (a, b, c) for
 * point p with respect to triangle ABC.
 *
 * <p> A, B, and C are points of the triangle ABC
 * in the counterclockwise direction.
 */
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

    return floatArrayOf(u, v, w)
}
