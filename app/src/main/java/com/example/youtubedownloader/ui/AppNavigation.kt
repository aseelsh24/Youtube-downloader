package com.example.youtubedownloader.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.youtubedownloader.downloader.DownloadScreen
import com.example.youtubedownloader.ui.main.MainScreen
import com.example.youtubedownloader.ui.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("downloads") { DownloadScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}
