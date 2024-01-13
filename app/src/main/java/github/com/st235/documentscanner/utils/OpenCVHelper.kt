package github.com.st235.documentscanner.utils

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat

class OpenCVHelper {

    companion object {
        fun load() {
            System.loadLibrary("OpenCVDocumentScannerLib")
        }
    }

    fun wrapPerspective(image: Bitmap,
                        topLeft: FloatArray,
                        topRight: FloatArray,
                        bottomRight: FloatArray,
                        bottomLeft: FloatArray,): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)

        val corners = FloatArray(8)

        corners[0] = topLeft[0]
        corners[1] = topLeft[1]
        corners[2] = topRight[0]
        corners[3] = topRight[1]
        corners[4] = bottomRight[0]
        corners[5] = bottomRight[1]
        corners[6] = bottomLeft[0]
        corners[7] = bottomLeft[1]

        val matOut = Mat()
        helloWorld(matIn.nativeObj, corners, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(matOut, out)
        Log.d("HelloWorld", "Bitmap size: ${out.width} ${out.height}")
        return out
    }

    external fun helloWorld(image: Long, corners: FloatArray, out: Long)
}