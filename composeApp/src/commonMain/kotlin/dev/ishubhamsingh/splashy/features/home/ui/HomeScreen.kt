package dev.ishubhamsingh.splashy.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.features.home.HomeViewModel
import dev.ishubhamsingh.splashy.models.Photo
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun HomeScreen(
    navigator: Navigator,
    viewModel: HomeViewModel = getViewModel(
        key = Screen.Home.route,
        factory = viewModelFactory { HomeViewModel() }
    )
) {

    val state by viewModel.state.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(items = state.photos, key = {it.id} ) {
                    PhotoCardItem(navigator, it)
                }
            }
        }

    }
}


@Composable
fun PhotoCardItem(
    navigator: Navigator,
    photo: Photo,
    heightDp: Dp = 320.dp,
    widthDp: Dp = 160.dp,
    padding: Dp = 4.dp,
) {

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(parseColor(photo.color))),
        modifier = Modifier
            .padding(vertical = padding, horizontal = padding)
            .fillMaxWidth()
            .height(heightDp)
            .background(
                color = Color(parseColor(photo.color)) ?: MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        KamelImage(resource =  asyncPainterResource(data = photo.urls.regular),
            contentDescription = photo.altDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
    }
}

fun parseColor(colorString: String): Int {
    if (colorString[0] == '#') { // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) { // Set the alpha value
            color = color or -0x1000000
        } else require(colorString.length == 9) { "Unknown color" }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}