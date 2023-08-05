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
package dev.ishubhamsingh.splashy.features.details

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.features.details.ui.DetailsEvent
import dev.ishubhamsingh.splashy.features.details.ui.DetailsState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsViewModel : ViewModel(), KoinComponent {
  private val unsplashRepository: UnsplashRepository by inject()

  private val _state = MutableStateFlow(DetailsState())
  val state = _state.asStateFlow()

  fun onEvent(event: DetailsEvent) {
    when (event) {
      DetailsEvent.ApplyAsWallpaper -> TODO()
      DetailsEvent.DownloadPhoto -> TODO()
      is DetailsEvent.LoadDetails -> {
        _state.update { detailsState -> detailsState.copy(id = event.id) }
        fetchPhotoDetails()
      }
    }
  }

  private fun fetchPhotoDetails(id: String = state.value.id ?: "") {
    viewModelScope.launch {
      unsplashRepository.getPhotoDetails(id).collect { networkResult ->
        when (networkResult) {
          is NetworkResult.Error -> {
            _state.update { detailsState ->
              detailsState.copy(networkError = networkResult.message)
            }
            Napier.e(networkResult.message.toString())
          }
          is NetworkResult.Loading -> {
            _state.update { detailsState -> detailsState.copy(isLoading = networkResult.isLoading) }
          }
          is NetworkResult.Success -> {
            _state.update { detailsState -> detailsState.copy(photo = networkResult.data) }
          }
        }
      }
    }
  }
}

enum class WallpaperScreenType {
  HOME_SCREEN,
  LOCK_SCREEN,
  BOTH,
  OTHER_APPLICATION
}
