package com.example.youtubedownloader.video_info

import com.example.youtubedownloader.model.VideoFormat
import com.example.youtubedownloader.model.VideoInfo
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoInfoExtractor {

    suspend fun fetchVideoInfo(url: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        try {
            val request = YoutubeDLRequest(url)
            val videoInfo = YoutubeDL.getInstance().getInfo(request)

            val formats = videoInfo.formats?.mapNotNull { format ->
                // Ignore formats without a download url
                if (format.url.isNullOrBlank()) {
                    return@mapNotNull null
                }
                VideoFormat(
                    formatId = format.formatId ?: "",
                    ext = format.ext ?: "",
                    resolution = format.resolution ?: "Audio",
                    fileSize = format.fileSize,
                    url = format.url!!,
                    isAudioOnly = format.acodec != "none" && format.vcodec == "none"
                )
            } ?: emptyList()

            Result.success(VideoInfo(videoInfo.title ?: "Untitled", formats))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
