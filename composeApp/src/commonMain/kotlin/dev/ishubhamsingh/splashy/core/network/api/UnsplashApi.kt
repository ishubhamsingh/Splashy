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
package dev.ishubhamsingh.splashy.core.network.api

import Splashy.composeApp.BuildConfig
import dev.ishubhamsingh.splashy.core.network.KtorLogger
import dev.ishubhamsingh.splashy.models.DownloadUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json

class UnsplashApi(private val httpClient: HttpClient) {

  private val client by lazy {
    httpClient.config {
      install(Logging) {
        logger = KtorLogger()
        level = LogLevel.ALL
      }
      install(ContentNegotiation) {
        json(
          Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
          }
        )
      }

      install(HttpRequestRetry) {
        maxRetries = 3
        retryIf { _, httpResponse -> !httpResponse.status.isSuccess() }
      }
    }
  }

  suspend fun fetchPhotos(page: Int): HttpResponse {
    return client.get {
      url {
        protocol = URLProtocol.HTTPS
        host = HOST_URL
        path("photos")
        parameters.append("client_id", BuildConfig.UNSPLASH_API_KEY)
        parameters.append("per_page", "10")
        parameters.append("order_by", "popular")
        parameters.append("orientation", "portrait")
        parameters.append("content_filter", "high")
        parameters.append("page", page.toString())
      }
    }
  }

  suspend fun searchPhotos(query: String, page: Int): HttpResponse {
    return client
      .get {
        url {
          protocol = URLProtocol.HTTPS
          host = HOST_URL
          path("search/photos")
          parameters.append("client_id", BuildConfig.UNSPLASH_API_KEY)
          parameters.append("query", query)
          parameters.append("per_page", "10")
          parameters.append("order_by", "relevant")
          parameters.append("orientation", "portrait")
          parameters.append("content_filter", "high")
          parameters.append("page", page.toString())
        }
      }
      .body()
  }

  suspend fun fetchPhotoDetails(id: String): HttpResponse {
    return client
      .get {
        url {
          protocol = URLProtocol.HTTPS
          host = HOST_URL
          path("/photos/$id")
          parameters.append("client_id", BuildConfig.UNSPLASH_API_KEY)
        }
      }
      .body()
  }

  suspend fun getDownloadUrl(url:String): DownloadUrl {
    return client.get {
      url(url).apply {
        parameter("client_id", BuildConfig.UNSPLASH_API_KEY)
      }
    }.body()
  }

  suspend fun downloadFile(url: String): ByteReadChannel {
    return client.get(url) {

      onDownload { bytesSentTotal, contentLength ->
        println("Downloaded $bytesSentTotal of $contentLength")
      }
    }.bodyAsChannel()
  }

  companion object {
    const val HOST_URL = "api.unsplash.com"
  }
}
