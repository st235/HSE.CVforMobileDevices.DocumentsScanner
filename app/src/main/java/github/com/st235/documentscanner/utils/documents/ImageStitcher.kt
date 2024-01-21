package github.com.st235.documentscanner.utils.documents

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ImageStitcher {

    private val nativePointer: Long

    init {
        nativePointer = init()
    }
    fun deinit() {
        deinit(nativePointer)
    }

    fun stitch(images: Array<Bitmap>): Bitmap {
        val matricesIn = Array(images.size) { i ->
            val matIn = Mat()
            Utils.bitmapToMat(images[i], matIn)
            Imgproc.cvtColor(matIn, matIn, Imgproc.COLOR_BGRA2BGR)
            matIn
        }

        val matricesInPtrs = LongArray(images.size) { i ->
            matricesIn[i].nativeObj
        }

        val matOut = Mat()
        stitch(nativePointer, matricesInPtrs, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(), Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    private external fun init(): Long
    private external fun deinit(pointer: Long)
    private external fun stitch(pointer: Long, images: LongArray, out: Long)
}