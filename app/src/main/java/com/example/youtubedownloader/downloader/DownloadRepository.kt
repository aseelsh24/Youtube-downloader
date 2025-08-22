package com.example.youtubedownloader.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.example.youtubedownloader.model.VideoFormat
import java.io.File

sealed class DownloadStatus {
    data class Progress(val progress: Int) : DownloadStatus()
    data class Completed(val fileUri: Uri) : DownloadStatus()
    data class Failed(val reason: String) : DownloadStatus()
}

class DownloadRepository(private val context: Context) {

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun startDownload(format: VideoFormat, title: String, downloadPath: String): Long {
        val fileExtension = if (format.isAudioOnly) "mp3" else "mp4"
        val fileName = "${title.replace("[^a-zA-Z0-9]".toRegex(), "_")}.$fileExtension"

        val destinationUri = if (downloadPath.startsWith("content://")) {
            val treeUri = Uri.parse(downloadPath)
            val docFile = DocumentFile.fromTreeUri(context, treeUri)
            val audioDir = docFile?.findFile("Audio") ?: docFile?.createDirectory("Audio")
            val videoDir = docFile?.findFile("Videos") ?: docFile?.createDirectory("Videos")

            val targetDir = if (format.isAudioOnly) audioDir else videoDir
            val file = targetDir?.createFile("video/$fileExtension", fileName)
            file?.uri
        } else {
            val downloadDir = File(downloadPath, if (format.isAudioOnly) "Audio" else "Videos")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            val file = File(downloadDir, fileName)
            Uri.fromFile(file)
        }


        val request = DownloadManager.Request(Uri.parse(format.url))
            .setTitle(title)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(destinationUri)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        return downloadManager.enqueue(request)
    }

    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }

    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            when (status) {
                DownloadManager.STATUS_RUNNING -> {
                    val totalBytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (totalBytes > 0) {
                        val downloadedBytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                        return DownloadStatus.Progress(progress)
                    }
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                    return DownloadStatus.Completed(Uri.parse(uriString))
                }
                DownloadManager.STATUS_FAILED -> {
                    val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                    return DownloadStatus.Failed("Download failed with reason: $reason")
                }
            }
        }
        cursor.close()
        return DownloadStatus.Failed("Download not found")
    }

}
