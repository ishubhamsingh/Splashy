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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TopicSubmissions(
  @SerialName("architecture-interior") val architectureInterior: TopicSubmissionStatus? = null,
  @SerialName("experimental") val experimental: TopicSubmissionStatus? = null,
  @SerialName("fashion-beauty") val fashionBeauty: TopicSubmissionStatus? = null,
  @SerialName("street-photography") val streetPhotography: TopicSubmissionStatus? = null,
  @SerialName("wallpapers") val wallpapers: TopicSubmissionStatus? = null,
  @SerialName("travel") val travel: TopicSubmissionStatus? = null
) {
  private val topicSubmissionMap =
    hashMapOf<TopicSubmissionStatus?, String>(
      wallpapers to "Wallpapers",
      experimental to "Experimental",
      travel to "Travel",
      fashionBeauty to "Fashion Beauty",
      streetPhotography to "Street Photography",
      architectureInterior to "Architecture Interior"
    )

  fun getApprovedTopics(): ArrayList<String> {
    val approvedList = arrayListOf<String>()
    topicSubmissionMap.entries.forEach { item ->
      item.key?.let { if (it.status == "approved") approvedList.add(item.value) }
    }
    return approvedList
  }

  override fun toString(): String {
    return Json.encodeToString(this)
  }
}
