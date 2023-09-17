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
import androidx.compose.ui.text.style.TextOverflow
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowIosBack
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.features.categoriesPhotos.CategoriesPhotosViewModel
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesPhotosScreen(
  navigator: Navigator,
  id: String,
  categoryType: CategoryType,
  name: String,
  viewModel: CategoriesPhotosViewModel
) {

  val state by viewModel.state.collectAsState()

  LaunchedEffect(id) { viewModel.onEvent(CategoriesPhotosEvent.Load(id, categoryType)) }

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
            modifier = Modifier.clickable { navigator.goBack() }
          )
        }
      )
    }
  ) {
    PhotoGridLayout(
      isRefreshing = state.isRefreshing,
      onRefresh = { viewModel.onEvent(CategoriesPhotosEvent.Refresh) },
      onSurfaceTouch = {},
      isPaginating = state.isPaginating,
      photos = state.photos,
      onLoadMore = {
        if (state.totalPages > state.currentPage) {
          viewModel.onEvent(CategoriesPhotosEvent.LoadMore)
        }
      },
      onItemSelected = { navigator.navigate(Screen.PhotoDetails.route.plus("/${it}")) },
      error = state.networkError,
      shouldShowSearch = false,
      modifier = Modifier.padding(it)
    )
  }
}

enum class CategoryType {
  COLLECTION,
  TOPIC
}
