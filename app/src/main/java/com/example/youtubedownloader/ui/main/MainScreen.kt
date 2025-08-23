package com.example.youtubedownloader.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.os.Environment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.youtubedownloader.R
import com.example.youtubedownloader.downloader.DownloadViewModel
import com.example.youtubedownloader.model.VideoFormat
import com.example.youtubedownloader.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    var url by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController.navigate("downloads") }) {
                        Icon(imageVector = Icons.Filled.Download, contentDescription = stringResource(id = R.string.downloads))
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = stringResource(id = R.string.settings))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(stringResource(id = R.string.enter_video_url)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.fetchVideoInfo(url) },
                enabled = url.isNotBlank() && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.download))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            uiState.videoInfo?.let { videoInfo ->
                val downloadViewModel: DownloadViewModel = viewModel()
                val settingsViewModel: SettingsViewModel = viewModel()
                val downloadPath by settingsViewModel.downloadPath.collectAsState()
                Text(text = videoInfo.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                VideoFormatList(
                    formats = videoInfo.formats,
                    onDownloadClick = { format ->
                        downloadViewModel.startDownload(
                            format = format,
                            title = videoInfo.title,
                            downloadPath = downloadPath ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                        )
                        navController.navigate("downloads")
                    }
                )
            }
        }
    }
}

@Composable
fun VideoFormatList(
    formats: List<VideoFormat>,
    onDownloadClick: (VideoFormat) -> Unit
) {
    LazyColumn {
        items(formats) { format ->
            FormatItem(format = format, onDownloadClick = onDownloadClick)
        }
    }
}

@Composable
fun FormatItem(
    format: VideoFormat,
    onDownloadClick: (VideoFormat) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (format.isAudioOnly) stringResource(id = R.string.audio_only) else format.resolution,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = android.text.format.Formatter.formatFileSize(LocalContext.current, format.fileSize),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onDownloadClick(format) }) {
                    Text(text = stringResource(id = R.string.download))
                }
            }
        }
    }
}
