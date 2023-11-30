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
package dev.ishubhamsingh.splashy.features.favourites

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.features.favourites.ui.FavouritesEvent
import dev.ishubhamsingh.splashy.features.favourites.ui.FavouritesState
import dev.ishubhamsingh.splashy.models.TopicFilter
import dev.ishubhamsingh.splashy.models.TopicSubmissions
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Created by Shubham Singh on 12/08/23. */
class FavouritesScreenModel(
  private val unsplashRepository: UnsplashRepository,
  val settingsUtils: SettingsUtils
) : ScreenModel {

  private val _state = MutableStateFlow(FavouritesState())
  val state = _state.asStateFlow()

  private var job: Job? = null

  fun onEvent(favouritesEvent: FavouritesEvent) {
    when (favouritesEvent) {
      is FavouritesEvent.Filter -> onFilterEvent(favouritesEvent.topic)
      FavouritesEvent.LoadFavourites -> fetchFavourites()
      FavouritesEvent.Refresh -> {
        _state.update { favouritesState ->
          favouritesState.copy(filterTopics = TopicSubmissions.TOPICS)
        }
        fetchFavourites()
      }
    }
  }

  private fun fetchFavourites() {
    cancelActiveJob()
    job =
      coroutineScope.launch {
        unsplashRepository.getFavourites().collect { result ->
          when (result) {
            is NetworkResult.Error -> {
              _state.update { favouritesState -> favouritesState.copy(error = result.message) }
            }
            is NetworkResult.Loading -> {
              _state.update { favouritesState ->
                favouritesState.copy(isRefreshing = result.isLoading)
              }
            }
            is NetworkResult.Success -> {
              result.data?.let {
                _state.update { favouritesState -> favouritesState.copy(favourites = it) }
                updateFilteredList(state.value.filterTopics)
              }
            }
          }
        }
      }
  }

  private fun onFilterEvent(topic: String) {
    val newFilterTopics =
      ArrayList(
        state.value.filterTopics.map {
          if (it.topic == topic) {
            TopicFilter(topic, !it.isSelected)
          } else {
            it
          }
        }
      )

    _state.update { favouritesState -> favouritesState.copy(filterTopics = newFilterTopics) }

    updateFilteredList(newFilterTopics)
  }

  private fun updateFilteredList(newFilterTopics: ArrayList<TopicFilter>) {
    val newFilteredFav =
      state.value.favourites.filter { fav ->
        fav.topicSubmissions?.containsAnyTopic(newFilterTopics) == true
      }
    _state.update { favouritesState ->
      favouritesState.copy(filteredFavourites = ArrayList(newFilteredFav))
    }
  }

  private fun cancelActiveJob() {
    if (job?.isActive == true) {
      job?.cancel()
    }
  }
}
