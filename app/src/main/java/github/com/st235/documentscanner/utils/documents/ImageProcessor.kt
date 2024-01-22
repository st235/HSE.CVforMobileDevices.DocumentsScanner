package github.com.st235.documentscanner.utils.documents

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ImageProcessor {

    enum class Binarization(val id: Int) {
        GLOBAL(0),
        ADAPTIVE_MEAN(1),
        ADAPTIVE_GAUSSIAN(2),
        OTSU(3),
        TRIANGLE(4),
    }

    enum class Filter(val id: Int) {
        BOX(0),
        GAUSSIAN(1),
        MEDIAN(2),
        BILATERAL(3),
    }

    enum class Denoising(val id: Int) {
        TVL1(0),
        FAST_NI(1),
    }

    enum class Contrast(val id: Int) {
        MULT(0),
        HISTOGRAM(1),
        CLAHE(2),
    }

    private val nativePointer: Long

    init {
        nativePointer = init()
    }

    fun deinit() {
        deinit(nativePointer)
    }

    fun rotate90DegreesClockwise(image: Bitmap): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)

        val matOut = Mat()
        rotate90(nativePointer, matIn.nativeObj, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    fun binarise(image: Bitmap, mode: Binarization): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)
        Imgproc.cvtColor(matIn, matIn, Imgproc.COLOR_BGRA2BGR)

        val matOut = Mat()
        binarization(nativePointer, matIn.nativeObj, mode.id, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    fun filter(image: Bitmap, mode: Filter): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)
        Imgproc.cvtColor(matIn, matIn, Imgproc.COLOR_BGRA2BGR)

        val matOut = Mat()
        filter(nativePointer, matIn.nativeObj, mode.id, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    fun denoise(image: Bitmap, mode: Denoising): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)
        Imgproc.cvtColor(matIn, matIn, Imgproc.COLOR_BGRA2BGR)

        val matOut = Mat()
        denoise(nativePointer, matIn.nativeObj, mode.id, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    fun enhanceContrast(image: Bitmap, mode: Contrast): Bitmap {
        val matIn = Mat()
        Utils.bitmapToMat(image, matIn)
        Imgproc.cvtColor(matIn, matIn, Imgproc.COLOR_BGRA2BGR)

        val matOut = Mat()
        enhanceContrast(nativePointer, matIn.nativeObj, mode.id, matOut.nativeObj)

        val out = Bitmap.createBitmap(matOut.cols(), matOut.rows(),Bitmap.Config.RGB_565)
        Utils.matToBitmap(matOut, out)
        return out
    }

    private external fun init(): Long
    private external fun deinit(pointer: Long)
    private external fun rotate90(pointer: Long, image: Long, out: Long)
    private external fun binarization(pointer: Long, image: Long, mode: Int, out: Long)
    private external fun filter(pointer: Long, image: Long, mode: Int, out: Long)
    private external fun denoise(pointer: Long, image: Long, mode: Int, out: Long)
    private external fun enhanceContrast(pointer: Long, image: Long, mode: Int, out: Long)
}