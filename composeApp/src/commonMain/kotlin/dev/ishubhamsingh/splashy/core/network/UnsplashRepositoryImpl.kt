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
import dev.ishubhamsingh.splashy.db.mappers.toFavouriteArrayList
import dev.ishubhamsingh.splashy.db.mappers.toFavouriteEntity
import dev.ishubhamsingh.splashy.db.mappers.toPhoto
import dev.ishubhamsingh.splashy.db.mappers.toPhotoArrayList
import dev.ishubhamsingh.splashy.db.mappers.toPhotoEntity
import dev.ishubhamsingh.splashy.db.mappers.toPhotoSearchCollection
import dev.ishubhamsingh.splashy.models.CollectionItem
import dev.ishubhamsingh.splashy.models.DownloadUrl
import dev.ishubhamsingh.splashy.models.Favourite
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.PhotoSearchCollection
import dev.ishubhamsingh.splashy.models.Topic
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class UnsplashRepositoryImpl(
  private val unsplashApi: UnsplashApi,
  private val splashyDatabase: SplashyDatabase
) : UnsplashRepository {
  override fun getPhotos(
    page: Int,
    fetchFromRemote: Boolean
  ): Flow<NetworkResult<ArrayList<Photo>>> =
    flow {
        val localCache = splashyDatabase.photoQueries.getPhotos().executeAsList()

        val isDbEmpty = localCache.isEmpty()
        val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

        if (shouldJustLoadFromCache) {
          emit(NetworkResult.Success(localCache.toPhotoArrayList())) // emit local cache
          return@flow
        }

        val remoteData =
          try {
            val request = unsplashApi.fetchPhotos(page) // Fetch from API
            if (request.status.value == 200) { // Call successful
              request.body<ArrayList<Photo>>()
            } else {
              Napier.e("errorCode: ${request.status.value}")
              Napier.e("errorMessage: ${request.status.description}")
              emit(NetworkResult.Error(request.status.description))
              null
            }
          } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
            null
          }

        remoteData?.let { photos ->
          withContext(Dispatchers.IO) {
            if (page == 1) {
              splashyDatabase.photoQueries.deleteAllPhotos() // Clear existing cache, if query reset
            }
            splashyDatabase.photoQueries.transaction {
              photos.forEach {
                splashyDatabase.photoQueries.insertPhoto(
                  it.toPhotoEntity()
                ) // Insert API response to cache
              }
            }
          }

          emit(NetworkResult.Success(photos)) // Emit API response
        }
      }
      .onStart {
        emit(NetworkResult.Loading(isLoading = true)) // Start Loading
      }
      .onCompletion {
        emit(NetworkResult.Loading(false)) // End Loading
      }
      .flowOn(Dispatchers.IO)

  override fun searchPhotos(
    searchQuery: String,
    page: Int,
    fetchFromRemote: Boolean
  ): Flow<NetworkResult<PhotoSearchCollection>> =
    flow {
        val localCache = splashyDatabase.photoQueries.getPhotos().executeAsList()

        val isDbEmpty = localCache.isEmpty()
        val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

        if (shouldJustLoadFromCache) {
          emit(
            NetworkResult.Success(localCache.toPhotoArrayList().toPhotoSearchCollection())
          ) // emit local cache
          return@flow
        }

        val remoteData =
          try {
            val request = unsplashApi.searchPhotos(searchQuery, page) // Fetch from API
            if (request.status.value == 200) { // Call successful
              request.body<PhotoSearchCollection>()
            } else {
              Napier.e("errorCode: ${request.status.value}")
              Napier.e("errorMessage: ${request.status.description}")
              emit(NetworkResult.Error(request.status.description))
              null
            }
          } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
            null
          }

        remoteData?.let { searchResult ->
          withContext(Dispatchers.IO) {
            if (page == 1) {
              splashyDatabase.photoQueries.deleteAllPhotos() // Clear existing cache, if query reset
            }
            splashyDatabase.photoQueries.transaction {
              searchResult.results.forEach {
                splashyDatabase.photoQueries.insertPhoto(
                  it.toPhotoEntity()
                ) // Insert API response to cache
              }
            }
          }

          emit(NetworkResult.Success(searchResult)) // Emit API response
        }
      }
      .onStart {
        emit(NetworkResult.Loading(isLoading = true)) // Start Loading
      }
      .onCompletion {
        emit(NetworkResult.Loading(false)) // End Loading
      }
      .flowOn(Dispatchers.IO)

  override fun getPhotoDetails(id: String): Flow<NetworkResult<Photo>> =
    flow {
        val localCache = splashyDatabase.photoQueries.getPhotosById(id).executeAsList()
        emit(NetworkResult.Success(localCache.getOrNull(0)?.toPhoto())) // emit local cache

        val isDbEmpty = localCache.isEmpty()
        val shouldJustLoadFromCache = !isDbEmpty

        if (shouldJustLoadFromCache) {
          return@flow
        }

        val remoteData =
          try {
            val request = unsplashApi.fetchPhotoDetails(id) // Fetch from API
            if (request.status.value == 200) { // Call successful
              request.body<Photo>()
            } else {
              Napier.e("errorCode: ${request.status.value}")
              Napier.e("errorMessage: ${request.status.description}")
              emit(NetworkResult.Error(request.status.description))
              null
            }
          } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
            null
          }

        remoteData?.let { photo ->
          withContext(Dispatchers.IO) {
            splashyDatabase.photoQueries.insertPhoto(
              photo.toPhotoEntity()
            ) // Insert API response to cache
          }

          emit(NetworkResult.Success(photo)) // Emit API response
        }
      }
      .onStart {
        emit(NetworkResult.Loading(isLoading = true)) // Start Loading
      }
      .onCompletion {
        emit(NetworkResult.Loading(false)) // End Loading
      }
      .flowOn(Dispatchers.IO)

  override fun getCollections(page: Int): Flow<NetworkResult<ArrayList<CollectionItem>>> =
    flow {
        try {
          val request = unsplashApi.fetchCollections(page)

          if (request.status.value == 200) {
            val response = request.body<ArrayList<CollectionItem>>()
            emit(NetworkResult.Success(data = response))
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart { emit(NetworkResult.Loading(isLoading = true)) }
      .onCompletion { emit(NetworkResult.Loading(isLoading = false)) }
      .flowOn(Dispatchers.IO)

  override fun getCollectionById(id: String): Flow<NetworkResult<CollectionItem>> =
    flow {
        try {
          val request = unsplashApi.fetchCollectionByID(id)

          if (request.status.value == 200) {
            val response = request.body<CollectionItem>()
            emit(NetworkResult.Success(data = response))
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart { emit(NetworkResult.Loading(isLoading = true)) }
      .onCompletion { emit(NetworkResult.Loading(isLoading = false)) }
      .flowOn(Dispatchers.IO)

  override fun getPhotosByCollection(id: String, page: Int): Flow<NetworkResult<ArrayList<Photo>>> =
    flow {
        try {
          val request = unsplashApi.fetchPhotosByCollection(id, page)

          if (request.status.value == 200) {
            val response = request.body<ArrayList<Photo>>()
            withContext(Dispatchers.IO) {
              if (page == 1) {
                splashyDatabase.photoQueries
                  .deleteAllPhotos() // Clear existing cache, if query reset
              }
              splashyDatabase.photoQueries.transaction {
                response.forEach {
                  splashyDatabase.photoQueries.insertPhoto(
                    it.toPhotoEntity()
                  ) // Insert API response to cache
                }
              }
            }
            emit(NetworkResult.Success(data = response))
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart { emit(NetworkResult.Loading(isLoading = true)) }
      .onCompletion { emit(NetworkResult.Loading(isLoading = false)) }
      .flowOn(Dispatchers.IO)

  override fun getTopics(page: Int): Flow<NetworkResult<ArrayList<Topic>>> =
    flow {
        try {
          val request = unsplashApi.fetchTopics(page)

          if (request.status.value == 200) {
            val response = request.body<ArrayList<Topic>>()
            emit(NetworkResult.Success(data = response))
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart { emit(NetworkResult.Loading(isLoading = true)) }
      .onCompletion { emit(NetworkResult.Loading(isLoading = false)) }
      .flowOn(Dispatchers.IO)

  override fun getTopicBySlug(slug: String): Flow<NetworkResult<Topic>> =
    flow {
        try {
          val request = unsplashApi.fetchTopicBySlug(slug)

          if (request.status.value == 200) {
            val response = request.body<Topic>()
            emit(NetworkResult.Success(data = response))
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart { emit(NetworkResult.Loading(isLoading = true)) }
      .onCompletion { emit(NetworkResult.Loading(isLoading = false)) }
      .flowOn(Dispatchers.IO)

  override fun getPhotosByTopic(slug: String, page: Int): Flow<NetworkResult<ArrayList<Photo>>> =
    flow {
        try {
          val request = unsplashApi.fetchPhotosByTopic(slug, page)

          if (request.status.value == 200) {
            val response = request.body<ArrayList<Photo>>()
            withContext(Dispatchers.IO) {
              if (page == 1) {
                splashyDatabase.photoQueries
                  .deleteAllPhotos() // Clear existing cache, if query reset
              }
              splashyDatabase.photoQueries.transaction {
                response.forEach {
                  splashyDatabase.photoQueries.insertPhoto(
                    it.toPhotoEntity()
                  ) // Insert API response to cache
                }
              }
            }
            emit(NetworkResult.Success(data = response))
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart { emit(NetworkResult.Loading(isLoading = true)) }
      .onCompletion { emit(NetworkResult.Loading(isLoading = false)) }
      .flowOn(Dispatchers.IO)

  override fun getDownloadUrl(url: String): Flow<NetworkResult<DownloadUrl>> =
    flow {
        try {
          val request = unsplashApi.getDownloadUrl(url) // Fetch from API
          if (request.status.value == 200) { // Call successful
            val data = request.body<DownloadUrl>()
            emit(NetworkResult.Success(data = data)) // emit response
          } else {
            Napier.e("errorCode: ${request.status.value}")
            Napier.e("errorMessage: ${request.status.description}")
            emit(NetworkResult.Error(request.status.description))
          }
        } catch (e: Exception) {
          e.printStackTrace()
          emit(NetworkResult.Error(message = e.message ?: "Couldn't load data"))
        }
      }
      .onStart {
        emit(NetworkResult.Loading(isLoading = true)) // Start Loading
      }
      .onCompletion {
        emit(NetworkResult.Loading(isLoading = false)) // Stop Loading
      }
      .flowOn(Dispatchers.IO)

  override fun addFavourite(favourite: Favourite): Flow<NetworkResult<Boolean>> =
    flow {
        splashyDatabase.favouriteQueries.insertFavourite(favourite.toFavouriteEntity())
        emit(NetworkResult.Success(true))
      }
      .flowOn(Dispatchers.IO)

  override fun removeFavourite(id: String): Flow<NetworkResult<Boolean>> =
    flow {
        splashyDatabase.favouriteQueries.deleteFavourite(id)
        emit(NetworkResult.Success(true))
      }
      .flowOn(Dispatchers.IO)

  override fun isFavourite(id: String): Flow<NetworkResult<Boolean>> =
    flow {
        val result = splashyDatabase.favouriteQueries.getFavouritesById(id).executeAsList()
        emit(NetworkResult.Success(result.isNotEmpty()))
      }
      .flowOn(Dispatchers.IO)

  override fun getFavourites(): Flow<NetworkResult<ArrayList<Favourite>>> =
    flow {
        val result = splashyDatabase.favouriteQueries.getFavourites().executeAsList()
        emit(NetworkResult.Success(result.toFavouriteArrayList()))
      }
      .flowOn(Dispatchers.IO)
}
