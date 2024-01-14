package github.com.st235.documentscanner.utils

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.util.Arrays

class OpenCVHelper {

    init {
        System.loadLibrary("OpenCVDocumentScannerLib")
    }

    data class Corners(
        val topLeft: FloatArray,
        val topRight: FloatArray,
        val bottomRight: FloatArray,
        val bottomLeft: FloatArray,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Corners

            if (!topLeft.contentEquals(other.topLeft)) return false
            if (!topRight.contentEquals(other.topRight)) return false
            if (!bottomRight.contentEquals(other.bottomRight)) return false
            return bottomLeft.contentEquals(other.bottomLeft)
        }

        override fun hashCode(): Int {
            var result = topLeft.contentHashCode()
            result = 31 * result + topRight.contentHashCode()
            result = 31 * result + bottomRight.contentHashCode()
            result = 31 * result + bottomLeft.contentHashCode()
            return result
        }
    }

    fun findCorners(image: Bitmap): Corners? {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)

        val rawCorners = findCorners(matIn.nativeObj) ?: return null

        Log.d("HelloWorld", "rawCorners: ${Arrays.toString(rawCorners)}")

        return Corners(
            topLeft = floatArrayOf(rawCorners[0], rawCorners[1]),
            topRight = floatArrayOf(rawCorners[2], rawCorners[3]),
            bottomRight = floatArrayOf(rawCorners[4], rawCorners[5]),
            bottomLeft = floatArrayOf(rawCorners[6], rawCorners[7]),
        )
    }

    fun wrapPerspective(
        image: Bitmap,
        corners: Corners,
    ): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)

        val rawCorners = FloatArray(8)

        rawCorners[0] = corners.topLeft[0]
        rawCorners[1] = corners.topLeft[1]
        rawCorners[2] = corners.topRight[0]
        rawCorners[3] = corners.topRight[1]
        rawCorners[4] = corners.bottomRight[0]
        rawCorners[5] = corners.bottomRight[1]
        rawCorners[6] = corners.bottomLeft[0]
        rawCorners[7] = corners.bottomLeft[1]

        val matOut = Mat()
        wrapPerspective(matIn.nativeObj, rawCorners, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    private external fun wrapPerspective(image: Long, corners: FloatArray, out: Long)
    private external fun findCorners(image: Long): FloatArray?
}