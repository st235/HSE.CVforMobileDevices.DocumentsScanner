package github.com.st235.documentscanner.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

class VideoFramesExtractor(
    private val context: Context,
    private val desiredFps: Int = DEFAULT_FPS
) {

    private companion object {
        const val DEFAULT_FPS = 24
    }

    data class Frame(
        val timestamp: Long,
        val content: Bitmap?
    )

    class VideoFramesIterator(
        context: Context,
        uri: Uri,
        desiredFps: Int
    ): Iterator<Frame> {

        private companion object {
            const val CONVERSION_FACTOR_SEC_TO_MS = 1000L
            const val CONVERSION_FACTOR_MS_TO_US = 1000L
        }

        private val mediaMetadataRetriever = MediaMetadataRetriever()

        private val frameDurationMs: Long = ((1.0 / desiredFps) * CONVERSION_FACTOR_SEC_TO_MS).toLong()

        private val durationMs: Long

        private var currentPlaybackTimeMs: Long = 0L

        init {
            mediaMetadataRetriever.setDataSource(context, uri)

            // Query duration after setDataSource is called.
            durationMs = mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }

        override fun hasNext(): Boolean {
            val hasNext = currentPlaybackTimeMs < durationMs
            if (!hasNext) {
                mediaMetadataRetriever.release()
            }
            return hasNext
        }

        override fun next(): Frame {
            val frameTimestamp = currentPlaybackTimeMs
            val frameBitmap = mediaMetadataRetriever.getFrameAtTime(frameTimestamp * CONVERSION_FACTOR_MS_TO_US)

            currentPlaybackTimeMs += frameDurationMs

            return Frame(frameTimestamp, frameBitmap)
        }
    }

    fun load(uri: Uri): VideoFramesIterator {
        return VideoFramesIterator(context, uri, desiredFps)
    }
}
