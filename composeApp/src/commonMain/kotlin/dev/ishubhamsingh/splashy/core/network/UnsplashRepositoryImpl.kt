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
package dev.ishubhamsingh.splashy.core.network

import dev.ishubhamsingh.splashy.core.di.Singleton
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.network.api.UnsplashApi
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.PhotoSearchCollection
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class UnsplashRepositoryImpl(private val unsplashApi: UnsplashApi) : UnsplashRepository {
  override fun getPhotos(page: Int, forceFetch: Boolean): Flow<NetworkResult<ArrayList<Photo>>> {
    return flow {
      emit(NetworkResult.Loading(isLoading = true))
      try {
        val request = unsplashApi.fetchPhotos(page)
        if (request.status.value == 200) {
          emit(NetworkResult.Success(request.body()))
        } else {
          emit(NetworkResult.Error(request.status.description))
        }
        emit(NetworkResult.Loading(isLoading = false))
      } catch (e: Exception) {
        emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        emit(NetworkResult.Loading(isLoading = false))
      }
    }
  }

  override fun searchPhotos(
    searchQuery: String,
    page: Int,
    forceFetch: Boolean
  ): Flow<NetworkResult<PhotoSearchCollection>> {
    return flow {
      emit(NetworkResult.Loading(isLoading = true))
      try {
        val request = unsplashApi.searchPhotos(searchQuery, page)
        if (request.status.value == 200) {
          emit(NetworkResult.Success(request.body()))
        } else {
          emit(NetworkResult.Error(request.status.description))
        }
        emit(NetworkResult.Loading(isLoading = false))
      } catch (e: Exception) {
        emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        emit(NetworkResult.Loading(isLoading = false))
      }
    }
  }
}
