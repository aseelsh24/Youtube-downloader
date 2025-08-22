package com.example.youtubedownloader.repository

import com.example.youtubedownloader.model.VideoInfo
import com.example.youtubedownloader.video_info.VideoInfoExtractor

class VideoInfoRepository {
    private val videoInfoExtractor = VideoInfoExtractor()

    suspend fun fetchVideoInfo(url: String): Result<VideoInfo> {
        return videoInfoExtractor.fetchVideoInfo(url)
    }
}
