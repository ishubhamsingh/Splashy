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
package dev.ishubhamsingh.splashy.core.domain

import dev.ishubhamsingh.splashy.models.CollectionItem
import dev.ishubhamsingh.splashy.models.DownloadUrl
import dev.ishubhamsingh.splashy.models.Favourite
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.PhotoSearchCollection
import dev.ishubhamsingh.splashy.models.Topic
import kotlinx.coroutines.flow.Flow

interface UnsplashRepository {
  fun getPhotos(page: Int, fetchFromRemote: Boolean = false): Flow<NetworkResult<ArrayList<Photo>>>

  fun searchPhotos(
    searchQuery: String,
    page: Int,
    fetchFromRemote: Boolean = false
  ): Flow<NetworkResult<PhotoSearchCollection>>

  fun getPhotoDetails(id: String): Flow<NetworkResult<Photo>>

  fun getCollections(page: Int): Flow<NetworkResult<ArrayList<CollectionItem>>>

  fun getCollectionById(id: String): Flow<NetworkResult<CollectionItem>>

  fun getPhotosByCollection(id: String, page: Int): Flow<NetworkResult<ArrayList<Photo>>>

  fun getTopics(page: Int): Flow<NetworkResult<ArrayList<Topic>>>

  fun getTopicBySlug(slug: String): Flow<NetworkResult<Topic>>

  fun getPhotosByTopic(slug: String, page: Int): Flow<NetworkResult<ArrayList<Photo>>>

  fun getDownloadUrl(url: String): Flow<NetworkResult<DownloadUrl>>

  fun addFavourite(favourite: Favourite): Flow<NetworkResult<Boolean>>

  fun removeFavourite(id: String): Flow<NetworkResult<Boolean>>

  fun isFavourite(id: String): Flow<NetworkResult<Boolean>>

  fun getFavourites(): Flow<NetworkResult<ArrayList<Favourite>>>
}
