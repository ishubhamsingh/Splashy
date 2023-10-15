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

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsScreenModel(val permissionsController: PermissionsController) :
  ScreenModel, KoinComponent {
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
          checkPermission { getDownloadUrl(shouldOpenFile = true) }
        } else {
          getDownloadUrl(isSaveToFile = false, wallpaperScreenType = event.wallpaperScreenType)
        }
      }
      DetailsEvent.DownloadPhoto -> {
        _state.update { detailsState -> detailsState.copy(isDownloading = true) }
        checkPermission { getDownloadUrl() }
      }
      is DetailsEvent.LoadDetails -> {
        _state.update { detailsState ->
          detailsState.copy(
            id = event.id,
            photo = event.photo,
            url = event.url,
            color = event.color,
            altDescription = event.altDescription
          )
        }
        isFavourite()
        if (event.photo == null) {
          fetchPhotoDetails()
        }
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

  private fun checkPermission(postPermissionGranted: () -> Unit) {
    coroutineScope.launch {
      try {
        if (fileUtils.shouldAskStorageRuntimePermission()) {
          permissionsController.providePermission(Permission.WRITE_STORAGE)
        }
        postPermissionGranted.invoke()
      } catch (deniedAlways: DeniedAlwaysException) {
        fileUtils.showMessage(
          "The storage permission is important for this action. Please grant the permission."
        )
        _state.update { detailsState ->
          detailsState.copy(isDownloading = false, isApplying = false)
        }
        permissionsController.openAppSettings()
      } catch (denied: DeniedException) {
        fileUtils.showMessage(
          "The storage permission is important for this action. Please grant the permission."
        )
        _state.update { detailsState ->
          detailsState.copy(isDownloading = false, isApplying = false)
        }
      }
    }
  }

  private fun fetchPhotoDetails(id: String = state.value.id ?: "") {
    coroutineScope.launch {
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
    shouldOpenFile: Boolean = false,
    wallpaperScreenType: WallpaperScreenType = WallpaperScreenType.OTHER_APPLICATION
  ) {
    photo?.links?.downloadLocation?.let {
      coroutineScope.launch {
        unsplashRepository.getDownloadUrl(it).collect { networkResult ->
          when (networkResult) {
            is NetworkResult.Error -> {
              _state.update { detailsState ->
                detailsState.copy(isDownloading = false, isApplying = false)
              }
              Napier.e(networkResult.message.toString())
            }
            is NetworkResult.Loading -> {}
            is NetworkResult.Success -> {
              val url = networkResult.data?.url
              url?.let {
                downloadPhoto(
                  url = url,
                  isSaveToFile = isSaveToFile,
                  shouldOpenFile = shouldOpenFile,
                  wallpaperScreenType = wallpaperScreenType
                ) { downloadedSize, totalSize ->
                  _state.update { detailsState ->
                    detailsState.copy(downloadProgress = (downloadedSize * 100 / totalSize).toInt())
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun downloadPhoto(
    url: String,
    isSaveToFile: Boolean,
    shouldOpenFile: Boolean,
    wallpaperScreenType: WallpaperScreenType = WallpaperScreenType.OTHER_APPLICATION,
    onDownload: (Long, Long) -> Unit
  ) {
    var byteArray: ByteArray? = null
    coroutineScope
      .launch(Dispatchers.IO) {
        byteArray = unsplashApi.downloadFile(url, onDownload).toByteArray()
      }
      .invokeOnCompletion {
        if (isSaveToFile) {
          saveToFile(byteArray, shouldOpenFile)
        } else {
          applyWallpaper(byteArray, wallpaperScreenType)
        }
      }
  }

  private fun updateSnackBarMessage(message: String) {
    _state.update { detailsState -> detailsState.copy(snackBarMessage = message) }
  }

  fun resetSnackBarMessage() {
    _state.update { detailsState -> detailsState.copy(snackBarMessage = null) }
  }

  private fun saveToFile(byteArray: ByteArray?, shouldOpenFile: Boolean) {
    coroutineScope
      .launch {
        byteArray?.let {
          fileUtils.saveByteArrayToFile(
            state.value.photo?.id ?: "image",
            it,
            shouldOpenFile,
            ::updateSnackBarMessage
          )
        }
      }
      .invokeOnCompletion {
        _state.update { detailsState ->
          detailsState.copy(isDownloading = false, isApplying = false)
        }
      }
  }

  private fun applyWallpaper(
    byteArray: ByteArray?,
    wallpaperScreenType: WallpaperScreenType = WallpaperScreenType.OTHER_APPLICATION
  ) {
    coroutineScope
      .launch {
        byteArray?.let {
          fileUtils.applyWallpaper(byteArray, wallpaperScreenType, ::updateSnackBarMessage)
        }
      }
      .invokeOnCompletion {
        _state.update { detailsState -> detailsState.copy(isApplying = false) }
      }
  }

  private fun addFavourite(favourite: Favourite? = state.value.photo?.toFavourite()) {
    favourite?.let {
      coroutineScope.launch {
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
    coroutineScope.launch {
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
    coroutineScope.launch {
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
