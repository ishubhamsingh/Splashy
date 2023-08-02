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
package dev.ishubhamsingh.splashy.features.home

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.features.home.ui.HomeEvent
import dev.ishubhamsingh.splashy.features.home.ui.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {
  private val unsplashRepository: UnsplashRepository by inject()

  private val _state = MutableStateFlow(HomeState())
  val state = _state.asStateFlow()

  init {
    fetchPhotos()
  }

  fun onEvent(event: HomeEvent) {
    when (event) {
      is HomeEvent.Load -> {
        fetchPhotos()
      }
      is HomeEvent.Refresh -> {
        resetPage()
        fetchPhotos()
      }
      is HomeEvent.OnSearchQueryChange -> {
        _state.update { homeState -> homeState.copy(searchQuery = event.query) }
        resetPage(false)
        fetchPhotos()
      }
      is HomeEvent.LoadMore -> {
        nextPage()
        fetchPhotos()
      }
    }
  }

  private fun nextPage() {
    _state.update { it.copy(currentPage = it.currentPage + 1) }
  }

  private fun resetPage(isResetQuery: Boolean = true) {
    _state.update {
      if (isResetQuery) {
        it.copy(currentPage = 1, totalPages = 0, searchQuery = null, photos = arrayListOf())
      } else {
        it.copy(currentPage = 1, totalPages = 0, photos = arrayListOf())
      }
    }
  }

  private fun fetchPhotos(page: Int = state.value.currentPage) {
    viewModelScope.launch {
      unsplashRepository
        .searchPhotos(
          if (state.value.searchQuery.isNullOrEmpty()) "wallpaper" else state.value.searchQuery!!,
          page
        )
        .collect { networkResult ->
          when (networkResult) {
            is NetworkResult.Success -> {
              networkResult.data?.let { resp ->
                _state.update { homeState ->
                  val current = homeState.photos
                  current.addAll(resp.results)
                  homeState.copy(photos = current, totalPages = resp.totalPages)
                }
              }
            }
            is NetworkResult.Error -> {
              _state.update { homeState -> homeState.copy(networkError = networkResult.message) }
            }
            is NetworkResult.Loading -> {
              _state.update { homeState ->
                if (homeState.currentPage == 1) {
                  homeState.copy(isRefreshing = networkResult.isLoading)
                } else {
                  homeState.copy(isLazyLoading = networkResult.isLoading)
                }
              }
            }
          }
        }
    }
  }
}
