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
package dev.ishubhamsingh.splashy.features.categoriesPhotos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowIosBack
import dev.ishubhamsingh.splashy.core.utils.UpdateSystemBars
import dev.ishubhamsingh.splashy.features.categoriesPhotos.CategoriesPhotosScreenModel
import dev.ishubhamsingh.splashy.features.details.ui.DetailsScreen
import dev.ishubhamsingh.splashy.goBack
import dev.ishubhamsingh.splashy.isDarkThemeState
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.ui.components.GoBack
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout

data class CategoriesPhotosScreen(
  val id: String,
  val categoryType: CategoryType,
  val name: String
) : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val screenModel = getScreenModel<CategoriesPhotosScreenModel>()
    UpdateSystemBars(
      statusBarColor = Color.Transparent,
      navigationBarColor = Color.Transparent,
      isDarkTheme = isDarkThemeState.value
    )
    val navigator = LocalNavigator.currentOrThrow
    val state by screenModel.state.collectAsState()

    LaunchedEffect(Unit) { screenModel.onEvent(CategoriesPhotosEvent.Load(id, categoryType)) }

    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = name,
              style = MaterialTheme.typography.titleLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          },
          navigationIcon = {
            Icon(
              imageVector = EvaIcons.Outline.ArrowIosBack,
              contentDescription = "back",
              modifier = Modifier.clickable { navigator.pop() }
            )
          }
        )
      }
    ) { paddingValues ->
      PhotoGridLayout(
        isRefreshing = state.isRefreshing,
        onRefresh = { screenModel.onEvent(CategoriesPhotosEvent.Refresh) },
        onSurfaceTouch = {},
        isPaginating = state.isPaginating,
        photos = state.photos,
        onLoadMore = {
          if (state.totalPages > state.currentPage) {
            screenModel.onEvent(CategoriesPhotosEvent.LoadMore)
          }
        },
        onItemSelected = {
          photo: Photo?,
          id: String?,
          color: String?,
          url: String?,
          altDescription: String? ->
          navigator.push(
            DetailsScreen(
              photo = photo,
              id = id,
              color = color,
              url = url,
              altDescription = altDescription
            )
          )
        },
        error = state.networkError,
        shouldShowSearch = false,
        modifier = Modifier.padding(paddingValues)
      )
    }

    if (goBack.value) {
      GoBack(navigator)
      goBack.value = false
    }
  }
}

enum class CategoryType {
  COLLECTION,
  TOPIC
}
