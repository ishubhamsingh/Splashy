package dev.ishubhamsingh.splashy

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import dev.ishubhamsingh.splashy.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() = AppTheme {

}

internal expect fun openUrl(url: String?)