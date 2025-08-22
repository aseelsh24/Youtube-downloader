package com.example.youtubedownloader.downloader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubedownloader.model.VideoFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DownloadItem(
    val downloadId: Long,
    val title: String,
    val format: VideoFormat,
    val status: DownloadStatus
)

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val downloadRepository = DownloadRepository(application)

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    fun startDownload(format: VideoFormat, title: String, downloadPath: String) {
        val downloadId = downloadRepository.startDownload(format, title, downloadPath)
        val newItem = DownloadItem(downloadId, title, format, DownloadStatus.Progress(0))
        _downloads.value = _downloads.value + newItem
    }

    fun updateDownloadStatus(downloadId: Long, status: DownloadStatus) {
        _downloads.value = _downloads.value.map { item ->
            if (item.downloadId == downloadId) {
                item.copy(status = status)
            } else {
                item
            }
        }
    }

    fun cancelDownload(downloadId: Long) {
        downloadRepository.cancelDownload(downloadId)
        _downloads.value = _downloads.value.filterNot { it.downloadId == downloadId }
    }

    fun checkDownloadsStatus() {
        viewModelScope.launch {
            _downloads.value.forEach { item ->
                if (item.status is DownloadStatus.Progress) {
                    val status = downloadRepository.getDownloadStatus(item.downloadId)
                    updateDownloadStatus(item.downloadId, status)
                }
            }
        }
    }
}
