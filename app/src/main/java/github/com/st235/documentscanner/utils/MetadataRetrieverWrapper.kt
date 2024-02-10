package github.com.st235.documentscanner.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build

class MetadataRetrieverWrapper(
    private val uri: Uri,
    private val context: Context,
    private val desiredFps: Int = DEFAULT_FPS
): Iterable<MetadataRetrieverWrapper.Frame> {
    private companion object {
        const val DEFAULT_FPS = 5

        const val CONVERSION_FACTOR_SEC_TO_MS = 1000L
        const val CONVERSION_FACTOR_MS_TO_US = 1000L

        const val FRAME_COUNT_NO_FRAMES = 0
    }

    data class Frame(
        val id: Long,
        val content: Bitmap
    )

    class TimestampVideoFramesIterator(
        desiredFps: Int,
        private val metadataRetrieverWrapper: MetadataRetrieverWrapper,
    ): Iterator<Frame> {


        private val frameDurationMs: Long = ((1.0 / desiredFps) * CONVERSION_FACTOR_SEC_TO_MS).toLong()

        private val durationMs: Long = metadataRetrieverWrapper.durationMs

        private var currentPlaybackTimeMs: Long = 0L

        override fun hasNext(): Boolean {
            return currentPlaybackTimeMs < durationMs
        }

        override fun next(): Frame {
            val frameTimestampMs = currentPlaybackTimeMs
            val frameBitmap = metadataRetrieverWrapper.getFrameBitmapAtTime(frameTimestampMs)

            if (frameBitmap == null) {
                throw IllegalStateException("Frame for $frameTimestampMs ms. is null")
            }

            currentPlaybackTimeMs += frameDurationMs

            return Frame(frameTimestampMs, frameBitmap)
        }
    }

    class IndexVideoFramesIterator(
        private val metadataRetrieverWrapper: MetadataRetrieverWrapper,
    ): Iterator<Frame> {

        private val framesCount = metadataRetrieverWrapper.frameCount

        private var currentFrame = 0

        override fun hasNext(): Boolean {
            return currentFrame < framesCount
        }

        override fun next(): Frame {
            val frameBitmap = metadataRetrieverWrapper.getFrameBitmapAtIndex(currentFrame)

            if (frameBitmap == null) {
                throw IllegalStateException("Frame for index $currentFrame is null")
            }

            currentFrame++

            return Frame(currentFrame.toLong(), frameBitmap)
        }
    }

    private val mediaMetadataRetriever = MediaMetadataRetriever()

    init {
        mediaMetadataRetriever.setDataSource(context, uri)
    }

    val isIndexingSupported: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT
                ) != null
            } else {
                false
            }
        }

    val frameCount: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT
                )?.toInt() ?: FRAME_COUNT_NO_FRAMES
            } else {
                0
            }
        }

    val durationMs: Long
        get() {
            return mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }

    val isEmpty: Boolean
        get() {
            return if (isIndexingSupported) {
                frameCount > 0
            } else {
                durationMs > 0
            }
        }

    fun getFrameBitmapById(frameId: Long): Bitmap? {
        return if (isIndexingSupported) {
            getFrameBitmapAtIndex(frameId.toInt())
        } else {
            getFrameBitmapAtTime(frameId)
        }
    }

    fun getFrameBitmapAtTime(timestampMs: Long): Bitmap? {
        return mediaMetadataRetriever.getFrameAtTime(timestampMs * CONVERSION_FACTOR_MS_TO_US)
    }

    fun getFrameBitmapAtIndex(index: Int): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mediaMetadataRetriever.getFrameAtIndex(index)
        } else {
            null
        }
    }

    fun release() {
        mediaMetadataRetriever.release()
    }

    override fun iterator(): Iterator<Frame> {
        return if (isIndexingSupported) {
            IndexVideoFramesIterator(this)
        } else {
            TimestampVideoFramesIterator(desiredFps, this)
        }
    }
}