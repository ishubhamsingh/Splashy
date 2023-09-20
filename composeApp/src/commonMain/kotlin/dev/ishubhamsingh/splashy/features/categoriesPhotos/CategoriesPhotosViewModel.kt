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
package dev.ishubhamsingh.splashy.features.categoriesPhotos

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.features.categoriesPhotos.ui.CategoriesPhotosEvent
import dev.ishubhamsingh.splashy.features.categoriesPhotos.ui.CategoriesPhotosState
import dev.ishubhamsingh.splashy.features.categoriesPhotos.ui.CategoryType
import dev.ishubhamsingh.splashy.features.home.LoadingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoriesPhotosViewModel : ViewModel(), KoinComponent {
  private val unsplashRepository: UnsplashRepository by inject()

  private val _state = MutableStateFlow(CategoriesPhotosState())
  val state = _state.asStateFlow()

  fun onEvent(event: CategoriesPhotosEvent) {
    when (event) {
      is CategoriesPhotosEvent.Load -> {
        if (state.value.id != event.id) {
          _state.update { CategoriesPhotosState() } // Reset state
        } else {
          return
        }
        _state.update { categoriesPhotosState ->
          categoriesPhotosState.copy(id = event.id, categoryType = event.type)
        }
        fetchCategoryItem()
        fetchPhotos(loadingType = LoadingType.REFRESH)
      }
      CategoriesPhotosEvent.LoadMore -> {
        nextPage()
        fetchPhotos(loadingType = LoadingType.PAGINATION)
      }
      CategoriesPhotosEvent.Refresh -> {
        resetPage()
        fetchCategoryItem()
        fetchPhotos(loadingType = LoadingType.REFRESH)
      }
    }
  }

  private fun fetchCategoryItem(type: CategoryType? = state.value.categoryType) {
    type?.let {
      if (it == CategoryType.TOPIC) {
        fetchTopicById()
      } else {
        fetchCollectionById()
      }
    }
  }

  private fun fetchPhotos(
    type: CategoryType? = state.value.categoryType,
    loadingType: LoadingType
  ) {
    type?.let {
      if (it == CategoryType.TOPIC) {
        fetchPhotosByTopic(loadingType = loadingType)
      } else {
        fetchPhotosByCollection(loadingType = loadingType)
      }
    }
  }

  private fun nextPage() {
    _state.update { it.copy(currentPage = it.currentPage + 1) }
  }

  private fun resetPage() {
    _state.update { it.copy(currentPage = 1, totalPages = 0, photos = arrayListOf()) }
  }

  private fun fetchPhotosByTopic(
    id: String? = state.value.id,
    page: Int = state.value.currentPage,
    loadingType: LoadingType
  ) {
    viewModelScope.launch {
      unsplashRepository.getPhotosByTopic(slug = id ?: "", page = page).collect { networkResult ->
        when (networkResult) {
          is NetworkResult.Error -> {
            _state.update { categoriesPhotoState ->
              categoriesPhotoState.copy(networkError = networkResult.message)
            }
          }
          is NetworkResult.Loading -> {
            _state.update { categoriesPhotosState ->
              when (loadingType) {
                LoadingType.REFRESH ->
                  categoriesPhotosState.copy(isRefreshing = networkResult.isLoading)
                LoadingType.SEARCH ->
                  categoriesPhotosState.copy(isRefreshing = networkResult.isLoading)
                LoadingType.PAGINATION ->
                  categoriesPhotosState.copy(isPaginating = networkResult.isLoading)
              }
            }
          }
          is NetworkResult.Success -> {
            networkResult.data?.let {
              _state.update { categoriesPhotosState ->
                if (categoriesPhotosState.photos.isEmpty().not()) {
                  val current = categoriesPhotosState.photos
                  current.addAll(it)
                  categoriesPhotosState.copy(photos = current)
                } else {
                  categoriesPhotosState.copy(photos = it)
                }
              }
            }
          }
        }
      }
    }
  }

  private fun fetchPhotosByCollection(
    id: String? = state.value.id,
    page: Int = state.value.currentPage,
    loadingType: LoadingType
  ) {
    viewModelScope.launch {
      unsplashRepository.getPhotosByCollection(id = id ?: "", page = page).collect { networkResult
        ->
        when (networkResult) {
          is NetworkResult.Error -> {
            _state.update { categoriesPhotosState ->
              categoriesPhotosState.copy(networkError = networkResult.message)
            }
          }
          is NetworkResult.Loading -> {
            _state.update { categoriesPhotosState ->
              when (loadingType) {
                LoadingType.REFRESH ->
                  categoriesPhotosState.copy(isRefreshing = networkResult.isLoading)
                LoadingType.SEARCH ->
                  categoriesPhotosState.copy(isRefreshing = networkResult.isLoading)
                LoadingType.PAGINATION ->
                  categoriesPhotosState.copy(isPaginating = networkResult.isLoading)
              }
            }
          }
          is NetworkResult.Success -> {
            networkResult.data?.let {
              _state.update { categoriesPhotosState ->
                if (categoriesPhotosState.photos.isEmpty().not()) {
                  val current = categoriesPhotosState.photos
                  current.addAll(it)
                  categoriesPhotosState.copy(photos = current)
                } else {
                  categoriesPhotosState.copy(photos = it)
                }
              }
            }
          }
        }
      }
    }
  }

  private fun fetchTopicById(id: String? = state.value.id) {
    viewModelScope.launch {
      unsplashRepository.getTopicBySlug(slug = id ?: "").collect { networkResult ->
        when (networkResult) {
          is NetworkResult.Error -> {}
          is NetworkResult.Loading -> {}
          is NetworkResult.Success -> {
            networkResult.data?.let {
              _state.update { categoriesPhotosState ->
                categoriesPhotosState.copy(topic = it, totalPages = it.totalPhotos?.div(10) ?: 1000)
              }
            }
          }
        }
      }
    }
  }

  private fun fetchCollectionById(id: String? = state.value.id) {
    viewModelScope.launch {
      unsplashRepository.getCollectionById(id = id ?: "").collect { networkResult ->
        when (networkResult) {
          is NetworkResult.Error -> {}
          is NetworkResult.Loading -> {}
          is NetworkResult.Success -> {
            networkResult.data?.let {
              _state.update { categoriesPhotosState ->
                categoriesPhotosState.copy(
                  collection = it,
                  totalPages = it.totalPhotos?.div(10) ?: 1000
                )
              }
            }
          }
        }
      }
    }
  }
}
