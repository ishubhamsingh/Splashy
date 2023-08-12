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

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.features.favourites.ui.FavouritesEvent
import dev.ishubhamsingh.splashy.features.favourites.ui.FavouritesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Created by Shubham Singh on 12/08/23. */
class FavouritesViewModel : ViewModel(), KoinComponent {
  private val unsplashRepository: UnsplashRepository by inject()

  private val _state = MutableStateFlow(FavouritesState())
  val state = _state.asStateFlow()

  private var job: Job? = null

  fun onEvent(favouritesEvent: FavouritesEvent) {
    when (favouritesEvent) {
      is FavouritesEvent.Filter -> TODO()
      FavouritesEvent.LoadFavourites -> fetchFavourites()
      FavouritesEvent.Refresh -> fetchFavourites()
    }
  }

  private fun fetchFavourites() {
    cancelActiveJob()
    job =
      viewModelScope.launch {
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
              }
            }
          }
        }
      }
  }

  private fun cancelActiveJob() {
    if (job?.isActive == true) {
      job?.cancel()
    }
  }
}
