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
import dev.ishubhamsingh.splashy.core.network.api.UnsplashApi
import dev.ishubhamsingh.splashy.core.utils.FileUtils
import dev.ishubhamsingh.splashy.db.mappers.toFavourite
import dev.ishubhamsingh.splashy.features.details.ui.DetailsEvent
import dev.ishubhamsingh.splashy.features.details.ui.DetailsState
import dev.ishubhamsingh.splashy.models.Favourite
import dev.ishubhamsingh.splashy.models.Photo
import io.github.aakira.napier.Napier
import io.ktor.util.toByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsViewModel : ViewModel(), KoinComponent {
  private val unsplashRepository: UnsplashRepository by inject()
  private val unsplashApi: UnsplashApi by inject()
  private val fileUtils: FileUtils by inject()

  private val _state = MutableStateFlow(DetailsState())
  val state = _state.asStateFlow()

  fun onEvent(event: DetailsEvent) {
    when (event) {
      is DetailsEvent.ApplyAsWallpaper -> {
        _state.update { detailsState -> detailsState.copy(isApplying = true) }
        if (event.wallpaperScreenType == WallpaperScreenType.OTHER_APPLICATION) {
          getDownloadUrl()
        } else {
          getDownloadUrl(isSaveToFile = false, wallpaperScreenType = event.wallpaperScreenType)
        }
      }
      DetailsEvent.DownloadPhoto -> {
        _state.update { detailsState -> detailsState.copy(isDownloading = true) }
        getDownloadUrl()
      }
      is DetailsEvent.LoadDetails -> {
        _state.update { detailsState -> detailsState.copy(id = event.id) }
        isFavourite()
        fetchPhotoDetails()
      }
      DetailsEvent.AddFavourite -> addFavourite()
      DetailsEvent.RemoveFavourite -> removeFavourite()
      DetailsEvent.DismissApplyWallpaperDialog -> {
        _state.update { detailsState -> detailsState.copy(shouldShowApplyWallpaperDialog = false) }
      }
      DetailsEvent.ShowApplyWallpaperDialog -> {
        _state.update { detailsState -> detailsState.copy(shouldShowApplyWallpaperDialog = true) }
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

  private fun getDownloadUrl(
    photo: Photo? = state.value.photo,
    isSaveToFile: Boolean = true,
    wallpaperScreenType: WallpaperScreenType = WallpaperScreenType.OTHER_APPLICATION
  ) {
    var url: String = ""
    photo?.links?.downloadLocation?.let {
      viewModelScope
        .launch {
          val response = unsplashApi.getDownloadUrl(it)
          url = response.url
        }
        .invokeOnCompletion {
          if (url.isNotEmpty()) downloadPhoto(url, isSaveToFile, wallpaperScreenType)
        }
    }
  }

  private fun downloadPhoto(
    url: String,
    isSaveToFile: Boolean,
    wallpaperScreenType: WallpaperScreenType = WallpaperScreenType.OTHER_APPLICATION
  ) {
    var byteArray: ByteArray? = null
    viewModelScope
      .launch { byteArray = unsplashApi.downloadFile(url).toByteArray() }
      .invokeOnCompletion {
        if (isSaveToFile) {
          saveToFile(byteArray)
        } else {
          applyWallpaper(byteArray, wallpaperScreenType)
        }
      }
  }

  private fun saveToFile(byteArray: ByteArray?) {
    viewModelScope
      .launch {
        byteArray?.let { fileUtils.saveByteArrayToFile(state.value.photo?.id ?: "image", it) }
      }
      .invokeOnCompletion {
        _state.update { detailsState -> detailsState.copy(isDownloading = false) }
      }
  }

  private fun applyWallpaper(
    byteArray: ByteArray?,
    wallpaperScreenType: WallpaperScreenType = WallpaperScreenType.OTHER_APPLICATION
  ) {
    viewModelScope
      .launch { byteArray?.let { fileUtils.applyWallpaper(byteArray, wallpaperScreenType) } }
      .invokeOnCompletion {
        _state.update { detailsState -> detailsState.copy(isApplying = false) }
      }
  }

  private fun addFavourite(favourite: Favourite? = state.value.photo?.toFavourite()) {
    favourite?.let {
      viewModelScope.launch {
        unsplashRepository.addFavourite(favourite).collect { result ->
          when (result) {
            is NetworkResult.Success -> {
              _state.update { detailsState -> detailsState.copy(isFavourite = true) }
            }
            else -> {}
          }
        }
      }
    }
  }

  private fun removeFavourite(id: String = state.value.id ?: "") {
    if (id.isEmpty()) return
    viewModelScope.launch {
      unsplashRepository.removeFavourite(id).collect { result ->
        when (result) {
          is NetworkResult.Success -> {
            _state.update { detailsState -> detailsState.copy(isFavourite = false) }
          }
          else -> {}
        }
      }
    }
  }

  private fun isFavourite(id: String = state.value.id ?: "") {
    if (id.isEmpty()) return
    viewModelScope.launch {
      unsplashRepository.isFavourite(id).collect { result ->
        when (result) {
          is NetworkResult.Success -> {
            _state.update { detailsState -> detailsState.copy(isFavourite = result.data == true) }
          }
          else -> {}
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
