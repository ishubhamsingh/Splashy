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
  @SerialName("travel") val travel: TopicSubmissionStatus? = null,
  @SerialName("arts-culture") val artsCulture : TopicSubmissionStatus? = null,
  @SerialName("athletics") val athletics: TopicSubmissionStatus? = null,
  @SerialName("color-of-water") val colorOfWater: TopicSubmissionStatus? = null,
  @SerialName("health") val health: TopicSubmissionStatus? = null,
  @SerialName("nature") val nature: TopicSubmissionStatus? = null,
  @SerialName("people") val people: TopicSubmissionStatus? = null,
  @SerialName("spirituality") val spirituality: TopicSubmissionStatus? = null,
  @SerialName("textures-patterns") val texturesPatterns: TopicSubmissionStatus? = null
) {
  private val topicSubmissionMap =
    hashMapOf(
      "Wallpapers" to wallpapers,
      "Experimental" to experimental,
      "Travel" to travel,
      "Fashion Beauty" to fashionBeauty,
      "Street Photography" to streetPhotography,
      "Architecture Interior" to architectureInterior,
      "Travel" to travel,
      "Arts Culture" to artsCulture,
      "Athletics" to athletics,
      "Color Of Water" to colorOfWater,
      "Health" to health,
      "Nature" to nature,
      "People" to people,
      "Spirituality" to spirituality,
      "Textures Patterns" to texturesPatterns
    )

  fun getApprovedTopics(): ArrayList<String> {
    val approvedList = arrayListOf<String>()
    topicSubmissionMap.entries.forEach { item ->
      item.let {
        if (it.value != null && it.value?.status == "approved") approvedList.add(item.key)
      }
    }
    return approvedList
  }

  override fun toString(): String {
    return Json.encodeToString(this)
  }

  fun containsAnyTopic(topicFilters: ArrayList<TopicFilter>): Boolean {
    if (topicFilters.any { it.isSelected }.not()) return true

    val selectedFilters: List<String> =
      topicFilters.mapNotNull { if (it.isSelected) it.topic else null }
    return selectedFilters.any { getApprovedTopics().contains(it) }
  }

  companion object {
    val TOPICS = ArrayList(TopicSubmissions().topicSubmissionMap.keys.map { TopicFilter(it) })
  }
}
