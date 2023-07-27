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
package dev.ishubhamsingh.splashy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ishubhamsingh.splashy.core.api.UnsplashApi
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.ui.theme.AppTheme
import dev.ishubhamsingh.splashy.ui.theme.seed
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient

@Composable
fun App(unsplashApi: UnsplashApi) {
  var photosResponse by remember { mutableStateOf<ArrayList<Photo>>(arrayListOf()) }
  LaunchedEffect(Unit) { photosResponse = unsplashApi.searchPhotos("hd wallpaper", 1).results }

  AppTheme(seedColor = seed) {
    Surface(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        photosResponse.forEach {
          KamelImage(
            resource = asyncPainterResource(data = it.urls.regular),
            contentDescription = it.altDescription,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop
          )
          Text(
            text = it.user.name ?: "",
            color = Color.White,
            modifier = Modifier.fillMaxWidth().background(color = Color(parseColor(it.color))),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
          )
        }
      }
    }
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

internal expect fun getPlatform(): String

internal expect fun getHttpClient(): HttpClient
