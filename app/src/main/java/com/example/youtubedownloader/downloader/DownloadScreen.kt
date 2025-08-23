package com.example.youtubedownloader.downloader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.content.Intent
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.youtubedownloader.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(navController: NavController, viewModel: DownloadViewModel = viewModel()) {
    val downloads by viewModel.downloads.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.downloads)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(downloads) { download ->
                DownloadItemRow(download = download, onCancel = { viewModel.cancelDownload(download.downloadId) })
            }
        }
    }
}

@Composable
fun DownloadItemRow(download: DownloadItem, onCancel: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = download.title, style = MaterialTheme.typography.bodyLarge)
                when (val status = download.status) {
                    is DownloadStatus.Progress -> {
                        LinearProgressIndicator(progress = status.progress / 100f)
                        Text(text = "${status.progress}%")
                    }
                    is DownloadStatus.Completed -> {
                        Text(stringResource(id = R.string.download_complete))
                        val context = LocalContext.current
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = if (download.format.isAudioOnly) "audio/mp3" else "video/mp4"
                            val fileUri = (download.status as DownloadStatus.Completed).fileUri
                            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(fileUri.path!!))
                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                            context.startActivity(Intent.createChooser(shareIntent, "Share File"))
                        }) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = stringResource(id = R.string.share))
                        }
                    }
                    is DownloadStatus.Failed -> Text(stringResource(id = R.string.download_failed), color = MaterialTheme.colorScheme.error)
                }
            }
            IconButton(onClick = onCancel) {
                Icon(imageVector = Icons.Filled.Cancel, contentDescription = stringResource(id = R.string.cancel))
            }
        }
    }
}
