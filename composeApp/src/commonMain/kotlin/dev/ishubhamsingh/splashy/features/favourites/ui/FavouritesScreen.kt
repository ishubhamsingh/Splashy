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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.features.favourites.FavouritesViewModel
import dev.ishubhamsingh.splashy.models.TopicFilter
import dev.ishubhamsingh.splashy.ui.components.PhotoGridLayout
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun FavouritesScreen(navigator: Navigator, viewModel: FavouritesViewModel) {
  val state by viewModel.state.collectAsState()

  LaunchedEffect(Unit) { viewModel.onEvent(FavouritesEvent.LoadFavourites) }

  Surface(
    color = MaterialTheme.colorScheme.surface,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TopicFilterLayout(
        topics = state.filterTopics,
        onFilterSelected = { viewModel.onEvent(FavouritesEvent.Filter(it)) }
      )

      PhotoGridLayout(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.onEvent(FavouritesEvent.Refresh) },
        onSurfaceTouch = {},
        searchQuery = state.searchQuery,
        isSearching = false,
        onSearchQueryChange = {},
        isPaginating = false,
        favourites = state.filteredFavourites,
        onLoadMore = {},
        onItemSelected = { navigator.navigate(Screen.PhotoDetails.route.plus("/${it}")) },
        error = "",
        isFavourite = true
      )
    }
  }
}

@Composable
fun TopicFilterLayout(topics: ArrayList<TopicFilter>, onFilterSelected: (String) -> Unit) {

  Row(
    modifier = Modifier.padding(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text("Topics:", style = MaterialTheme.typography.bodyLarge)

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
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.secondary
      ),
    border =
      FilterChipDefaults.filterChipBorder(
        borderColor = MaterialTheme.colorScheme.surfaceTint,
        borderWidth = 2.dp,
      )
  )
}
