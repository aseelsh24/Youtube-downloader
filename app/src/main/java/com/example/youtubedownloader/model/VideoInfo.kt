package com.example.youtubedownloader.model

data class VideoInfo(
    val title: String,
    val formats: List<VideoFormat>
)

data class VideoFormat(
    val formatId: String,
    val ext: String,
    val resolution: String,
    val fileSize: Long,
    val url: String,
    val isAudioOnly: Boolean
)
