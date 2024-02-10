package github.com.st235.documentscanner.utils.documents

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import github.com.st235.documentscanner.utils.MetadataRetrieverWrapper
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class KeyFrameDetector(
    private val context: Context
) {

    fun getKeyFrame(uri: Uri,
                    similarityThreshold: Double = 0.95): Bitmap? {
        val videoRetriever = MetadataRetrieverWrapper(uri, context)

        if (!videoRetriever.isEmpty) {
            return null
        }

        val frameIds = mutableListOf<Long>()
        val frameDiffs = mutableListOf<Double>()

        val framesIterator = videoRetriever.iterator()
        var currentFrame = framesIterator.next()
        frameIds.add(currentFrame.id)

        while (framesIterator.hasNext()) {
            val nextFrame = framesIterator.next()
            frameIds.add(currentFrame.id)

            val frameDiff = getFramesDiff(currentFrame.content, nextFrame.content)
            frameDiffs.add(frameDiff)

            currentFrame = nextFrame
        }

        val groupedFrames = mutableListOf<List<Long>>()
        var currentFrameGroup = mutableListOf<Long>()

        for (i in 0 until frameDiffs.size) {
            val currentFrameId = frameIds[i]

            currentFrameGroup.add(currentFrameId)

            val diff = frameDiffs[i]
            if (diff < similarityThreshold) {
                groupedFrames.add(currentFrameGroup)
                currentFrameGroup = mutableListOf()
            }
        }

        currentFrameGroup.add(frameIds.last())
        groupedFrames.add(currentFrameGroup)

        // First comes the biggest group.
        groupedFrames.sortByDescending { it.size }

        if (groupedFrames.isEmpty()) {
            return null
        }

        val biggestGroup = groupedFrames.first()
        val centralFrameId = biggestGroup[biggestGroup.size / 2]
        val centralFrameBitmap = videoRetriever.getFrameBitmapById(centralFrameId)

        videoRetriever.release()
        return centralFrameBitmap
    }

    private fun getFramesDiff(currentFrame: Bitmap,
                              nextFrame: Bitmap): Double {
        val matCurrentFrame = Mat()
        Utils.bitmapToMat(currentFrame, matCurrentFrame)
        Imgproc.cvtColor(matCurrentFrame, matCurrentFrame, Imgproc.COLOR_BGRA2BGR)

        val matNextFrame = Mat()
        Utils.bitmapToMat(nextFrame, matNextFrame)
        Imgproc.cvtColor(matNextFrame, matNextFrame, Imgproc.COLOR_BGRA2BGR)

        return getFramesDiff(matCurrentFrame.nativeObj, matNextFrame.nativeObj)
    }

    private external fun getFramesDiff(currentFramePtr: Long, nextFramePtr: Long): Double
}