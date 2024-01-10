package github.com.st235.documentscanner.presentation.components

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.annotation.Px
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class CroppingViewport(
    val leftTopCorner: PointF,
    val leftBottomCorner: PointF,
    val rightTopCorner: PointF,
    val rightBottomCorner: PointF,
)

private fun Array<PointF>.toCroppingViewport(): CroppingViewport {
    if (size != 4) {
        throw IllegalArgumentException("Expected array of size 4, but got $size")
    }

    return CroppingViewport(
        leftTopCorner = PointF(get(0).x, get(0).y),
        leftBottomCorner = PointF(get(1).x, get(1).y),
        rightBottomCorner = PointF(get(2).x, get(2).y),
        rightTopCorner = PointF(get(3).x, get(3).y),
    )
}

private fun CroppingViewport.toArray(): Array<PointF> {
    return arrayOf(
        PointF(leftTopCorner.x, leftTopCorner.y),
        PointF(leftBottomCorner.x, leftBottomCorner.y),
        PointF(rightBottomCorner.x, rightBottomCorner.y),
        PointF(rightTopCorner.x, rightTopCorner.y),
    )
}

class CropViewDragController(
    initialViewport: CroppingViewport,
    @Px private val cornerDragThreshold: Float,
    private val onCornersChanged: (CroppingViewport) -> Unit,
) {

    private companion object {
        const val NULL_CORNER = -1

        fun squareDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return (x1 - x2).pow(2) + (y1 - y2).pow(2)
        }
    }

    val corners = initialViewport.toArray()

    private var draggingCornerState by mutableIntStateOf(NULL_CORNER)

    var viewport by mutableStateOf(initialViewport)
        private set

    private var bounds = Rect.Zero

    fun onBoundsChanged(newBounds: Rect) {
        bounds = newBounds
    }

    fun onStartDragging(x: Float, y: Float): Boolean {
        val draggingCandidate = findCorner(x, y)

        if (draggingCandidate == NULL_CORNER) {
            return false
        }

        draggingCornerState = draggingCandidate
        return true
    }

    fun onDragging(x: Float, y: Float): Boolean {
        if (draggingCornerState == NULL_CORNER) {
            return false
        }

        corners[draggingCornerState] = PointF(
            max(bounds.left, min(bounds.right, x)),
            max(bounds.top, min(bounds.bottom, y)),
        )
        viewport = corners.toCroppingViewport()

        onCornersChanged(viewport)
        return true
    }

    fun onStopDragging() {
        draggingCornerState = NULL_CORNER
    }

    private fun findCorner(x: Float, y: Float): Int {
        for (i in corners.indices) {
            val corner = corners[i]
            val squareDistance = squareDistance(corner.x, corner.y, x, y)
            if (squareDistance <= cornerDragThreshold.pow(2)) {
                return i
            }
        }
        return NULL_CORNER
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
                onCornersChanged
            )
        )
    }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
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

        val newWidth: Float
        val newHeight: Float
        if (image.width > image.height) {
            // iw / cw = ih / ch
            val scaleFactor = image.width.toFloat() / canvasWidth
            newWidth = canvasWidth
            newHeight = image.height / scaleFactor
        } else {
            val scaleFactor = image.height.toFloat() / canvasHeight
            newHeight = canvasHeight
            newWidth = image.width / scaleFactor
        }

        val previewBitmap = Bitmap.createScaledBitmap(
            image,
            newWidth.toInt(),
            newHeight.toInt(),
            false
        )

        val offsetX = (canvasWidth - previewBitmap.width) / 2
        val offsetY = (canvasHeight - previewBitmap.height) / 2

        dragController.onBoundsChanged(Rect(Offset(offsetX, offsetY), Size(newWidth, newHeight)))

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
            moveTo(viewport.leftTopCorner.x, viewport.leftTopCorner.y)
            lineTo(viewport.leftBottomCorner.x, viewport.leftBottomCorner.y)
            lineTo(viewport.rightBottomCorner.x, viewport.rightBottomCorner.y)
            lineTo(viewport.rightTopCorner.x, viewport.rightTopCorner.y)
            lineTo(viewport.leftTopCorner.x, viewport.leftTopCorner.y)
        }

        drawPath(
            path,
            color = overlayColor
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.leftTopCorner.x, viewport.leftTopCorner.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.leftBottomCorner.x, viewport.leftBottomCorner.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.rightBottomCorner.x, viewport.rightBottomCorner.y),
        )

        drawCircle(
            color = cornerColor,
            radius = cornerRadiusPx,
            center = Offset(viewport.rightTopCorner.x, viewport.rightTopCorner.y),
        )
    }
}
