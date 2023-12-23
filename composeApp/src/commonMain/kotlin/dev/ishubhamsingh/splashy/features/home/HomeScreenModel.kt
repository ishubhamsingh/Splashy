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

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.features.home.ui.HomeEvent
import dev.ishubhamsingh.splashy.features.home.ui.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenModel(
  private val unsplashRepository: UnsplashRepository,
  val settingsUtils: SettingsUtils
) : ScreenModel {

  private val _state = MutableStateFlow(HomeState())
  val state = _state.asStateFlow()

  private var job: Job? = null

  init {
    onEvent(HomeEvent.Load)
  }

  fun onEvent(event: HomeEvent) {
    when (event) {
      HomeEvent.Load -> {
        getPhotosFromApi(forceFetch = true)
      }
      HomeEvent.Refresh -> {
        resetPage()
        getPhotosFromApi(forceFetch = true)
      }
      is HomeEvent.OnSearchQueryChange -> {
        _state.update { homeState -> homeState.copy(searchQuery = event.query) }
        resetPage(false)
        getPhotosFromApi(loadingType = LoadingType.SEARCH, forceFetch = true)
      }
      HomeEvent.LoadMore -> {
        nextPage()
        getPhotosFromApi(loadingType = LoadingType.PAGINATION, forceFetch = true)
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

  private fun getPhotosFromApi(
    loadingType: LoadingType = LoadingType.REFRESH,
    forceFetch: Boolean = false
  ) {
    if (state.value.searchQuery.isNullOrEmpty().not()) {
      searchPhotos(loadingType = loadingType, forceFetch = forceFetch)
    } else {
      fetchPhotos(loadingType = loadingType, forceFetch = forceFetch)
    }
  }

  private fun searchPhotos(
    page: Int = state.value.currentPage,
    loadingType: LoadingType = LoadingType.REFRESH,
    forceFetch: Boolean = false
  ) {
    cancelActiveJob() // Cancel ongoing call before launching new
    job =
      screenModelScope.launch(Dispatchers.IO) {
        unsplashRepository
          .searchPhotos(
            if (state.value.searchQuery.isNullOrEmpty()) "wallpaper" else state.value.searchQuery!!,
            page,
            forceFetch
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
                  when (loadingType) {
                    LoadingType.REFRESH -> homeState.copy(isRefreshing = networkResult.isLoading)
                    LoadingType.PAGINATION -> homeState.copy(isPaginating = networkResult.isLoading)
                    LoadingType.SEARCH -> homeState.copy(isSearching = networkResult.isLoading)
                  }
                }
              }
            }
          }
      }
  }

  private fun fetchPhotos(
    page: Int = state.value.currentPage,
    loadingType: LoadingType = LoadingType.REFRESH,
    forceFetch: Boolean = false
  ) {
    cancelActiveJob() // Cancel ongoing call before launching new
    job =
      screenModelScope.launch(Dispatchers.IO) {
        unsplashRepository.getPhotos(page, forceFetch).collect { networkResult ->
          when (networkResult) {
            is NetworkResult.Success -> {
              networkResult.data?.let { resp ->
                _state.update { homeState ->
                  val current = homeState.photos
                  current.addAll(resp)
                  homeState.copy(photos = current, totalPages = 1000)
                }
              }
            }
            is NetworkResult.Error -> {
              _state.update { homeState -> homeState.copy(networkError = networkResult.message) }
            }
            is NetworkResult.Loading -> {
              _state.update { homeState ->
                when (loadingType) {
                  LoadingType.REFRESH -> homeState.copy(isRefreshing = networkResult.isLoading)
                  LoadingType.PAGINATION -> homeState.copy(isPaginating = networkResult.isLoading)
                  LoadingType.SEARCH -> homeState.copy(isSearching = networkResult.isLoading)
                }
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

enum class LoadingType {
  REFRESH,
  SEARCH,
  PAGINATION
}
