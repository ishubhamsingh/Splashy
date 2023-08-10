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
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowheadUp
import compose.icons.evaicons.outline.CloseCircle
import compose.icons.evaicons.outline.Search
import dev.ishubhamsingh.splashy.features.home.HomeViewModel
import dev.ishubhamsingh.splashy.ui.components.OnBottomReached
import dev.ishubhamsingh.splashy.ui.components.PhotoCardItem
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigator: Navigator, viewModel: HomeViewModel) {
  val state by viewModel.state.collectAsState()

  val pullRefreshState =
    rememberPullRefreshState(state.isRefreshing, { viewModel.onEvent(HomeEvent.Refresh) })
  val lazyGridState = rememberLazyGridState()
  val threshold = 5
  val isFabVisible by
    remember(threshold) { derivedStateOf { lazyGridState.firstVisibleItemIndex > threshold } }

  val focusManager = LocalFocusManager.current

  Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier.fillMaxSize().clickable { focusManager.clearFocus() }
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(Modifier.pullRefresh(pullRefreshState)) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(4.dp),
          state = lazyGridState
        ) {
          item(span = { GridItemSpan(this.maxLineSpan) }) { SearchBar(state, viewModel) }
          items(items = state.photos) { PhotoCardItem(navigator, it) }
          if (state.isPaginating) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
              Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp),
                  color = MaterialTheme.colorScheme.secondary
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
          backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.secondary
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
      contentColor = MaterialTheme.colorScheme.secondary,
      elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 15.dp)
    ) {
      Icon(imageVector = EvaIcons.Outline.ArrowheadUp, contentDescription = "scroll up")
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(state: HomeState, viewModel: HomeViewModel) {
  val interactionSource = remember { MutableInteractionSource() }
  val enabled = true
  val singleLine = true

  BasicTextField(
    value = state.searchQuery ?: "",
    onValueChange = { viewModel.onEvent(HomeEvent.OnSearchQueryChange(it)) },
    modifier =
      Modifier.fillMaxWidth()
        .padding(16.dp)
        .height(48.dp)
        .border(
          2.dp,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
          shape = CircleShape
        ),
    interactionSource = interactionSource,
    singleLine = singleLine,
    enabled = enabled,
    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp),
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
  ) {
    OutlinedTextFieldDefaults.DecorationBox(
      value = state.searchQuery ?: "",
      innerTextField = it,
      enabled = enabled,
      singleLine = singleLine,
      visualTransformation = VisualTransformation.None,
      interactionSource = interactionSource,
      placeholder = {
        Text("Search", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))
      },
      leadingIcon = {
        Icon(
          imageVector = EvaIcons.Outline.Search,
          contentDescription = "search",
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.primary
        )
      },
      trailingIcon = {
          AnimatedVisibility(
            visible = state.searchQuery.isNullOrEmpty().not()
                    && state.isSearching.not(),
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            IconButton(
              onClick = {
                viewModel.onEvent(HomeEvent.OnSearchQueryChange(null))
              },
            ) {
              Icon(
                imageVector = EvaIcons.Outline.CloseCircle,
                contentDescription = "clear",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }

        AnimatedVisibility(
          visible = state.isSearching,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
          )
        }
      },
      contentPadding = PaddingValues(0.dp),
      container = {
        OutlinedTextFieldDefaults.ContainerBox(
          enabled,
          false,
          interactionSource,
          colors =
            TextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              cursorColor = MaterialTheme.colorScheme.primary,
              unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
              focusedTextColor = MaterialTheme.colorScheme.onSurface,
              unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
              focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
              unfocusedIndicatorColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent
            ),
          unfocusedBorderThickness = 0.dp,
          focusedBorderThickness = 0.dp
        )
      },
    )
  }
}
