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
package dev.ishubhamsingh.splashy.features.categories.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ishubhamsingh.splashy.features.categories.CategoriesViewModel
import dev.ishubhamsingh.splashy.models.CollectionItem
import dev.ishubhamsingh.splashy.models.Topic
import dev.ishubhamsingh.splashy.ui.components.PhotoCardItem
import dev.ishubhamsingh.splashy.ui.components.parseColor
import dev.ishubhamsingh.splashy.ui.theme.getLatoBold
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun CategoriesScreen(navigator: Navigator, viewModel: CategoriesViewModel) {
  val state by viewModel.state.collectAsState()

  Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier.padding(16.dp)
  ) {
    CategoriesList(state.collections, state.topics)
  }
}

@Composable
fun CategoriesList(collections: ArrayList<CollectionItem>, topics: ArrayList<Topic>) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item(content = {Text(text = "Collections", style = MaterialTheme.typography.displayMedium)})
    item(content = {
      CollectionsList(collections)
    })
    item(content = {
      Spacer(modifier = Modifier.size(16.dp))
    })
    item(content = {Text(text = "Topics", style = MaterialTheme.typography.displayMedium)})
    items(topics) {
      PhotoCardItem(
        onItemSelected = {},
        id = it.slug ?: "",
        color = it.coverPhoto?.color,
        url = it.coverPhoto?.urls?.regular,
        altDescription = it.coverPhoto?.altDescription,
        heightDp = 150.dp,
        modifier = Modifier.fillMaxWidth(),
        shouldShowOverlay = true,
        overlayTitle = it.title ?: "",
        overlaySubtitle = "${it.totalPhotos} Photos"
      )
    }
  }
}

@Composable
fun CollectionsList(collections: ArrayList<CollectionItem>) {
  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(collections) {
      PhotoCardItem(
        onItemSelected = {},
        id = it.id ?: "",
        color = it.coverPhoto?.color,
        url = it.coverPhoto?.urls?.regular,
        altDescription = it.coverPhoto?.description,
        heightDp = 100.dp,
        widthDp = 180.dp,
        shouldShowOverlay = true,
        overlayTitle = it.title ?: "",
        overlaySubtitle = "${it.totalPhotos} Photos"
      )
    }
  }
}
