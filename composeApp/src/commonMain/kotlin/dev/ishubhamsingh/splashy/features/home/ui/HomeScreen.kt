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
package dev.ishubhamsingh.splashy.features.home.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.features.home.HomeViewModel
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigator: Navigator, viewModel: HomeViewModel) {
  val state by viewModel.state.collectAsState()

  val focusManager = LocalFocusManager.current

  PhotoGridLayout(
    isRefreshing = state.isRefreshing,
    onRefresh = { viewModel.onEvent(HomeEvent.Refresh) },
    onSurfaceTouch = { focusManager.clearFocus() },
    searchQuery = state.searchQuery,
    isSearching = state.isSearching,
    onSearchQueryChange = { viewModel.onEvent(HomeEvent.OnSearchQueryChange(it)) },
    isPaginating = state.isPaginating,
    photos = state.photos,
    onLoadMore = {
      if (state.totalPages > state.currentPage) {
        viewModel.onEvent(HomeEvent.LoadMore)
      }
    },
    onItemSelected = { navigator.navigate(Screen.PhotoDetails.route.plus("/${it}")) },
    error = state.networkError,
    isFavourite = false
  )
}
