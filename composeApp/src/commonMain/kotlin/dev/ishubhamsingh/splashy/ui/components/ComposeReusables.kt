/*
 * Copyright 2023 Shubham Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ishubhamsingh.splashy.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowBack
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.core.network.KtorLogger
import dev.ishubhamsingh.splashy.models.Photo
import io.kamel.core.config.DefaultCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.CacheControl
import io.ktor.http.isSuccess
import moe.tlaster.precompose.navigation.Navigator

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
    modifier =
      Modifier.padding(vertical = padding, horizontal = padding)
        .fillMaxWidth()
        .height(heightDp)
        .background(
          color = Color(parseColor(photo.color)) ?: MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(16.dp)
        )
        .clickable { navigator.navigate(Screen.PhotoDetails.route.plus("/${photo.id}")) }
  ) {
    CompositionLocalProvider(LocalKamelConfig provides getKamelConfig(photo.urls.regular)) {
      KamelImage(
        resource = asyncPainterResource(data = photo.urls.regular),
        contentDescription = photo.altDescription,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        animationSpec = tween()
      )
    }
  }
}

@Composable
fun getKamelConfig(url: String): KamelConfig {
  val kamelConfig = KamelConfig {
    takeFrom(KamelConfig.Default)
    imageBitmapCacheSize = DefaultCacheSize
    imageBitmapDecoder()

    httpFetcher {
      defaultRequest { url(url) }

      CacheControl.MaxAge(maxAgeSeconds = 10000)

      install(HttpRequestRetry) {
        maxRetries = 3
        retryIf { _, httpResponse -> !httpResponse.status.isSuccess() }
      }

      install(Logging) {
        logger = KtorLogger()
        level = LogLevel.INFO
      }
    }
  }

  return kamelConfig
}

@Composable
fun LazyGridState.OnBottomReached(loadMore: () -> Unit) {
  val shouldLoadMore = remember {
    derivedStateOf {
      val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true

      lastVisibleItem.index == layoutInfo.totalItemsCount - 1
    }
  }

  // Convert the state into a cold flow and collect
  LaunchedEffect(shouldLoadMore) {
    snapshotFlow { shouldLoadMore.value }
      .collect {
        // if should load more, then invoke loadMore
        if (it) loadMore()
      }
  }
}

@Composable
fun BackButton(modifier: Modifier = Modifier, navigator: Navigator) {
  Box(
    modifier =
      modifier
        .padding(16.dp)
        .size(48.dp)
        .background(
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
          shape = CircleShape
        ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = EvaIcons.Outline.ArrowBack,
      contentDescription = "back",
      modifier = Modifier.padding(8.dp).size(24.dp).clickable { navigator.goBack() },
      tint = MaterialTheme.colorScheme.primary
    )
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
