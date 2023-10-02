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

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import compose.icons.evaicons.fill.Folder
import compose.icons.evaicons.outline.Folder
import dev.ishubhamsingh.splashy.core.presentation.CommonRes
import dev.ishubhamsingh.splashy.core.utils.UpdateSystemBars
import dev.ishubhamsingh.splashy.core.utils.getNonPremiumCollections
import dev.ishubhamsingh.splashy.features.categories.CategoriesScreenModel
import dev.ishubhamsingh.splashy.features.categoriesPhotos.ui.CategoriesPhotosScreen
import dev.ishubhamsingh.splashy.features.categoriesPhotos.ui.CategoryType
import dev.ishubhamsingh.splashy.models.CollectionItem
import dev.ishubhamsingh.splashy.models.Topic
import dev.ishubhamsingh.splashy.ui.components.CategoriesCardItem

/** Created by Shubham Singh on 29/09/23. */
object CategoriesTab : Tab {

  override val options: TabOptions
    @Composable
    get() {
      val isSelected = LocalTabNavigator.current.current == this
      val title = CommonRes.lbl_categories
      val icon =
        rememberVectorPainter(if (isSelected) EvaIcons.Fill.Folder else EvaIcons.Outline.Folder)

      return remember { TabOptions(index = 1u, title, icon) }
    }

  @OptIn(ExperimentalMaterialApi::class)
  @Composable
  override fun Content() {
    UpdateSystemBars(
      statusBarColor = Color.Transparent,
      navigationBarColor = Color.Transparent,
      isDarkTheme = isSystemInDarkTheme()
    )
    val navigator = LocalNavigator.currentOrThrow.parent
    val screenModel = getScreenModel<CategoriesScreenModel>()
    val state by screenModel.state.collectAsState()

    val pullRefreshState =
      rememberPullRefreshState(
        refreshing = state.isCategoriesLoading,
        onRefresh = { screenModel.onEvent(CategoriesEvent.Refresh) }
      )

    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
      Box(Modifier.pullRefresh(pullRefreshState)) {
        if (state.isCategoriesLoading) {
          Column(
            modifier =
              Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
        CategoriesList(
          state.collections,
          state.topics,
          onItemSelected = { id, name, type ->
            navigator?.push(CategoriesPhotosScreen(id, type, name))
          }
        )
        PullRefreshIndicator(
          state.isCategoriesLoading,
          pullRefreshState,
          Modifier.align(Alignment.TopCenter),
          backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.secondary
        )
      }
    }
  }

  @Composable
  fun CategoriesList(
    collections: ArrayList<CollectionItem>,
    topics: ArrayList<Topic>,
    onItemSelected: (String, String, CategoryType) -> Unit
  ) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      item(
        content = {
          Text(
            text = CommonRes.lbl_collections_title,
            style = MaterialTheme.typography.displayMedium
          )
        }
      )
      item(content = { CollectionsList(collections, onItemSelected) })
      item(content = { Spacer(modifier = Modifier.size(16.dp)) })
      item(
        content = {
          Text(text = CommonRes.lbl_topics_title, style = MaterialTheme.typography.displayMedium)
        }
      )
      items(topics) {
        CategoriesCardItem(
          onItemSelected = onItemSelected,
          id = it.slug ?: "",
          name = it.title ?: "",
          categoryType = CategoryType.TOPIC,
          color = it.coverPhoto?.color,
          url = it.coverPhoto?.urls?.regular,
          altDescription = it.coverPhoto?.altDescription,
          heightDp = 150.dp,
          modifier = Modifier.fillMaxWidth(),
          shouldShowOverlay = true,
          overlayTitle = it.title ?: "",
          overlaySubtitle = CommonRes.lbl_photos_count.replace("%count", it.totalPhotos.toString())
        )
      }
    }
  }

  @Composable
  fun CollectionsList(
    collections: ArrayList<CollectionItem>,
    onItemSelected: (String, String, CategoryType) -> Unit
  ) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(collections.getNonPremiumCollections()) {
        CategoriesCardItem(
          onItemSelected = onItemSelected,
          id = it.id ?: "",
          name = it.title ?: "",
          categoryType = CategoryType.COLLECTION,
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
}
