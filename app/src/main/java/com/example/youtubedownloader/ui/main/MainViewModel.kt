package com.example.youtubedownloader.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubedownloader.model.VideoInfo
import com.example.youtubedownloader.repository.VideoInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainScreenState(
    val isLoading: Boolean = false,
    val videoInfo: VideoInfo? = null,
    val error: String? = null
)

class MainViewModel : ViewModel() {

    private val repository = VideoInfoRepository()

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    fun fetchVideoInfo(url: String) {
        viewModelScope.launch {
            _uiState.value = MainScreenState(isLoading = true)
            val result = repository.fetchVideoInfo(url)
            _uiState.value = result.fold(
                onSuccess = { info -> MainScreenState(videoInfo = info) },
                onFailure = { error -> MainScreenState(error = error.message) }
            )
        }
    }
}
