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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Heart
import compose.icons.evaicons.outline.Checkmark
import compose.icons.evaicons.outline.Heart
import dev.ishubhamsingh.splashy.core.presentation.CommonRes
import dev.ishubhamsingh.splashy.core.utils.UpdateSystemBars
import dev.ishubhamsingh.splashy.features.details.ui.DetailsScreen
import dev.ishubhamsingh.splashy.features.favourites.FavouritesScreenModel
import dev.ishubhamsingh.splashy.isDarkThemeState
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.TopicFilter
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout

/** Created by Shubham Singh on 29/09/23. */
object FavouritesTab : Tab {

  override val options: TabOptions
    @Composable
    get() {
      val isSelected = LocalTabNavigator.current.current == this
      val title = CommonRes.lbl_favourites
      val icon =
        rememberVectorPainter(if (isSelected) EvaIcons.Fill.Heart else EvaIcons.Outline.Heart)

      return remember { TabOptions(index = 2u, title, icon) }
    }

  @Composable
  override fun Content() {
    val screenModel = getScreenModel<FavouritesScreenModel>()
    UpdateSystemBars(
      statusBarColor = Color.Transparent,
      navigationBarColor = Color.Transparent,
      isDarkTheme = isDarkThemeState.value
    )
    val navigator = LocalNavigator.currentOrThrow.parent
    val state by screenModel.state.collectAsState()

    LaunchedEffect(Unit) { screenModel.onEvent(FavouritesEvent.LoadFavourites) }

    Surface(
      color = MaterialTheme.colorScheme.surface,
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TopicFilterLayout(
          topics = state.filterTopics,
          onFilterSelected = { screenModel.onEvent(FavouritesEvent.Filter(it)) }
        )

        PhotoGridLayout(
          isRefreshing = state.isRefreshing,
          onRefresh = { screenModel.onEvent(FavouritesEvent.Refresh) },
          onSurfaceTouch = {},
          isPaginating = false,
          favourites = state.filteredFavourites,
          onLoadMore = {},
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
          error = "",
        )
      }
    }
  }

  @Composable
  fun TopicFilterLayout(topics: ArrayList<TopicFilter>, onFilterSelected: (String) -> Unit) {

    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        CommonRes.lbl_filter,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 4.dp)
      )

      LazyRow(
        contentPadding = PaddingValues(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(items = topics, key = { it.topic }) { topic ->
          TopicFilterItem(topic.topic, topic.isSelected, onFilterSelected)
        }
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun TopicFilterItem(topic: String, isSelected: Boolean, onFilterSelected: (String) -> Unit) {

    FilterChip(
      selected = isSelected,
      onClick = { onFilterSelected.invoke(topic) },
      shape = CircleShape,
      label = { Text(text = topic, style = MaterialTheme.typography.bodyMedium) },
      colors =
        FilterChipDefaults.filterChipColors(
          containerColor = MaterialTheme.colorScheme.surface,
          labelColor = MaterialTheme.colorScheme.surfaceTint,
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
      border =
        FilterChipDefaults.filterChipBorder(
          borderColor = MaterialTheme.colorScheme.surfaceTint,
          borderWidth = 2.dp,
          selectedBorderColor = MaterialTheme.colorScheme.primaryContainer,
          selectedBorderWidth = 2.dp
        ),
      leadingIcon = {
        AnimatedVisibility(isSelected) {
          Icon(
            imageVector = EvaIcons.Outline.Checkmark,
            contentDescription = "check",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    )
  }
}
