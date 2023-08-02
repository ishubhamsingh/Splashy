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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowIosUpward
import dev.ishubhamsingh.splashy.features.home.HomeViewModel
import dev.ishubhamsingh.splashy.ui.components.OnBottomReached
import dev.ishubhamsingh.splashy.ui.components.PhotoCardItem
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navigator: Navigator, viewModel: HomeViewModel) {
  val state by viewModel.state.collectAsState()

  val pullRefreshState =
    rememberPullRefreshState(state.isRefreshing, { viewModel.onEvent(HomeEvent.Refresh) })
  val lazyGridState = rememberLazyGridState()
  val threshold = 5
  val isFabVisible by
    remember(threshold) { derivedStateOf { lazyGridState.firstVisibleItemIndex > threshold } }

  LaunchedEffect(Unit) { viewModel.onEvent(HomeEvent.Load) }

  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(Modifier.pullRefresh(pullRefreshState)) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(4.dp),
          state = lazyGridState
        ) {
          items(items = state.photos, key = { it.id }) { PhotoCardItem(navigator, it) }
          if (state.isLazyLoading) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
              Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
        lazyGridState.OnBottomReached {
          // do on load more
          if (state.totalPages > state.currentPage) {
            viewModel.onEvent(HomeEvent.LoadMore)
          }
        }
        PullRefreshIndicator(
          state.isRefreshing,
          pullRefreshState,
          Modifier.align(Alignment.TopCenter),
          backgroundColor = MaterialTheme.colorScheme.onSurface,
          contentColor = MaterialTheme.colorScheme.inverseOnSurface
        )
        ScrollToTopFab(
          isFabVisible,
          lazyGridState,
          modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp),
        )
      }
    }
  }
}

@Composable
fun BoxScope.ScrollToTopFab(
  isFabVisible: Boolean,
  lazyGridState: LazyGridState,
  modifier: Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val density = LocalDensity.current

  AnimatedVisibility(
    modifier = modifier,
    visible = isFabVisible,
    enter = slideInVertically { with(density) { 40.dp.roundToPx() } } + fadeIn(),
    exit = fadeOut(animationSpec = keyframes { this.durationMillis = 120 })
  ) {
    FloatingActionButton(
      onClick = { coroutineScope.launch { lazyGridState.animateScrollToItem(0) } },
      backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.secondary
    ) {
      Icon(imageVector = EvaIcons.Outline.ArrowIosUpward, contentDescription = "scroll up")
    }
  }
}
