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
package dev.ishubhamsingh.splashy.core.api

import Splashy.composeApp.BuildConfig
import dev.ishubhamsingh.splashy.core.di.Singleton
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.PhotoSearchCollection
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.path
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class UnsplashApi(private val client: HttpClient) {
  suspend fun fetchPhotos(page: Int): ArrayList<Photo> {
    return client
      .get {
        url {
          protocol = URLProtocol.HTTPS
          host = HOST_URL
          path("photos")
          parameters.append("client_id", BuildConfig.UNSPLASH_API_KEY)
          parameters.append("page", page.toString())
        }
      }
      .body()
  }

  suspend fun searchPhotos(query: String, page: Int): PhotoSearchCollection {
    return client
      .get {
        url {
          protocol = URLProtocol.HTTPS
          host = HOST_URL
          path("search/photos")
          parameters.append("client_id", BuildConfig.UNSPLASH_API_KEY)
          parameters.append("query", query)
          parameters.append("page", page.toString())
        }
      }
      .body()
  }

  companion object {
    const val HOST_URL = "api.unsplash.com"
  }
}
