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
package dev.ishubhamsingh.splashy.features.favourites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.features.favourites.FavouritesViewModel
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun FavouritesScreen(navigator: Navigator, viewModel: FavouritesViewModel) {
  val state by viewModel.state.collectAsState()

  LaunchedEffect(Unit) { viewModel.onEvent(FavouritesEvent.LoadFavourites) }

  PhotoGridLayout(
    isRefreshing = state.isRefreshing,
    onRefresh = { viewModel.onEvent(FavouritesEvent.Refresh) },
    onSurfaceTouch = {},
    searchQuery = state.searchQuery,
    isSearching = false,
    onSearchQueryChange = {},
    isPaginating = false,
    favourites = state.favourites,
    onLoadMore = {},
    onItemSelected = { navigator.navigate(Screen.PhotoDetails.route.plus("/${it}")) },
    error = "",
    isFavourite = true
  )
}
