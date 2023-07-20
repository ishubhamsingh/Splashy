package dev.ishubhamsingh.splashy

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import dev.ishubhamsingh.splashy.theme.AppTheme
import dev.ishubhamsingh.splashy.theme.seed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() = AppTheme(seedColor = seed) {

}

internal expect fun getPlatform(): String