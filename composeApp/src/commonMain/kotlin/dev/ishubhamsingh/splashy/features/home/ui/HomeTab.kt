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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Home
import compose.icons.evaicons.outline.Home
import dev.ishubhamsingh.splashy.core.presentation.CommonRes
import dev.ishubhamsingh.splashy.core.utils.UpdateSystemBars
import dev.ishubhamsingh.splashy.features.details.ui.DetailsScreen
import dev.ishubhamsingh.splashy.features.home.HomeScreenModel
import dev.ishubhamsingh.splashy.isDarkThemeState
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout

/** Created by Shubham Singh on 29/09/23. */
object HomeTab : Tab {

  override val options: TabOptions
    @Composable
    get() {
      val isSelected = LocalTabNavigator.current.current == this
      val title = CommonRes.lbl_home
      val icon =
        rememberVectorPainter(if (isSelected) EvaIcons.Fill.Home else EvaIcons.Outline.Home)

      return remember { TabOptions(index = 0u, title, icon) }
    }

  @Composable
  override fun Content() {
    val screenModel = getScreenModel<HomeScreenModel>()
    UpdateSystemBars(
      statusBarColor = Color.Transparent,
      navigationBarColor = Color.Transparent,
      isDarkTheme = isDarkThemeState.value
    )
    val navigator = LocalNavigator.currentOrThrow.parent
    val state by screenModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    PhotoGridLayout(
      isRefreshing = state.isRefreshing,
      onRefresh = { screenModel.onEvent(HomeEvent.Refresh) },
      onSurfaceTouch = { focusManager.clearFocus() },
      searchQuery = state.searchQuery,
      isSearching = state.isSearching,
      onSearchQueryChange = { screenModel.onEvent(HomeEvent.OnSearchQueryChange(it)) },
      isPaginating = state.isPaginating,
      photos = state.photos,
      onLoadMore = {
        if (state.totalPages > state.currentPage) {
          screenModel.onEvent(HomeEvent.LoadMore)
        }
      },
      onItemSelected = {
        photo: Photo?,
        id: String?,
        color: String?,
        url: String?,
        altDescription: String? ->
        navigator?.push(
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
      shouldShowSearch = true
    )
  }
}
