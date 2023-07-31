package dev.ishubhamsingh.splashy.features.favourites.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun FavouritesScreen(
    navigator: Navigator
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Hello Favourites")
    }
}