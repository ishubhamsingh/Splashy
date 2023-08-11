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

import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.network.api.UnsplashApi
import dev.ishubhamsingh.splashy.db.SplashyDatabase
import dev.ishubhamsingh.splashy.db.toPhoto
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.PhotoSearchCollection
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnsplashRepositoryImpl(
  private val unsplashApi: UnsplashApi,
  private val splashyDatabase: SplashyDatabase
) : UnsplashRepository {
  override fun getPhotos(page: Int, forceFetch: Boolean): Flow<NetworkResult<ArrayList<Photo>>> {
    return flow {
      emit(NetworkResult.Loading(isLoading = true))
      val data = splashyDatabase.photoQueries.getPhotos().executeAsList()

      if (forceFetch || data.isEmpty()) {
        try {
          val request = unsplashApi.fetchPhotos(page)
          if (request.status.value == 200) {
            val resp = request.body<ArrayList<Photo>>()
            if (page == 1) {
              splashyDatabase.photoQueries.deleteAllPhotos()
            }
            splashyDatabase.photoQueries.transaction {
              resp.forEach {
                splashyDatabase.photoQueries.insertPhoto(
                  id = it.id,
                  color = it.color,
                  altDescription = it.altDescription,
                  description = it.description,
                  likes = it.likes,
                  width = it.width,
                  height = it.height,
                  links = it.links,
                  topicSubmissions = it.topicSubmissions,
                  urls = it.urls,
                  user = it.user,
                  promotedAt = it.promotedAt,
                  createdAt = it.createdAt,
                  updatedAt = it.updatedAt
                )
              }
            }
            emit(NetworkResult.Success(resp))
          } else {
            emit(NetworkResult.Error(request.status.description))
          }
          emit(NetworkResult.Loading(isLoading = false))
        } catch (e: Exception) {
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
          emit(NetworkResult.Loading(isLoading = false))
        }
      } else {
        val response = data.map { it.toPhoto() }
        val list = arrayListOf<Photo>()
        list.addAll(response)

        emit(NetworkResult.Loading(isLoading = false))
        emit(NetworkResult.Success(list))
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
      val data = splashyDatabase.photoQueries.getPhotos().executeAsList()
      if (forceFetch || data.isEmpty()) {
        try {
          val request = unsplashApi.searchPhotos(searchQuery, page)
          if (request.status.value == 200) {
            val resp = request.body<PhotoSearchCollection>()
            val photoList = resp.results

            if (page == 1) {
              splashyDatabase.photoQueries.deleteAllPhotos()
            }
            splashyDatabase.photoQueries.transaction {
              photoList.forEach {
                splashyDatabase.photoQueries.insertPhoto(
                  id = it.id,
                  color = it.color,
                  altDescription = it.altDescription,
                  description = it.description,
                  likes = it.likes,
                  width = it.width,
                  height = it.height,
                  links = it.links,
                  topicSubmissions = it.topicSubmissions,
                  urls = it.urls,
                  user = it.user,
                  promotedAt = it.promotedAt,
                  createdAt = it.createdAt,
                  updatedAt = it.updatedAt
                )
              }
            }

            emit(NetworkResult.Success(resp))
          } else {
            emit(NetworkResult.Error(request.status.description))
          }
          emit(NetworkResult.Loading(isLoading = false))
        } catch (e: Exception) {
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
          emit(NetworkResult.Loading(isLoading = false))
        }
      } else {
        val photoData = data.map { it.toPhoto() }
        val list = arrayListOf<Photo>()
        list.addAll(photoData.take(10))

        val response = PhotoSearchCollection(results = list, total = list.size, totalPages = 2)
        emit(NetworkResult.Loading(isLoading = false))
        emit(NetworkResult.Success(response))
      }
    }
  }

  override fun getPhotoDetails(id: String): Flow<NetworkResult<Photo>> {
    return flow {
      emit(NetworkResult.Loading(isLoading = true))
      val data = splashyDatabase.photoQueries.getPhotosById(id).executeAsList()
      if (data.isEmpty()) {
        try {
          val request = unsplashApi.fetchPhotoDetails(id)
          if (request.status.value == 200) {
            emit(NetworkResult.Success(request.body()))
          } else {
            emit(NetworkResult.Error(request.status.value.toString()))
          }
          emit(NetworkResult.Loading(isLoading = false))
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data", exception = e))
          emit(NetworkResult.Loading(isLoading = false))
        }
      } else {
        emit(NetworkResult.Loading(isLoading = false))
        emit(NetworkResult.Success(data[0].toPhoto()))
      }
    }
  }
}
