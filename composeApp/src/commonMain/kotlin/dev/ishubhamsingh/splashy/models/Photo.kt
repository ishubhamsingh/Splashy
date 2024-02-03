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
package dev.ishubhamsingh.splashy.models

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
  @SerialName("alt_description") val altDescription: String? = "",
  @SerialName("color") val color: String = "",
  @SerialName("created_at") val createdAt: String? = "",
  @SerialName("description") val description: String? = "",
  @SerialName("height") val height: Int = 0,
  @SerialName("id") val id: String = "",
  @SerialName("likes") val likes: Int = 0,
  @SerialName("links") val links: Links? = Links(),
  @SerialName("promoted_at") val promotedAt: String? = "",
  @SerialName("topic_submissions") val topicSubmissions: TopicSubmissions? = TopicSubmissions(),
  @SerialName("updated_at") val updatedAt: String? = "",
  @SerialName("urls") val urls: Urls? = Urls(),
  @SerialName("user") val user: User? = User(),
  @SerialName("width") val width: Int = 0
): JavaSerializable
