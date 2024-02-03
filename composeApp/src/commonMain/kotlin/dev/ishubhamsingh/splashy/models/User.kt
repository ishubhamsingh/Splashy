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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class User(
  @SerialName("accepted_tos") val acceptedTos: Boolean? = false,
  @SerialName("bio") val bio: String? = "",
  @SerialName("first_name") val firstName: String? = "",
  @SerialName("for_hire") val forHire: Boolean? = false,
  @SerialName("id") val id: String? = "",
  @SerialName("instagram_username") val instagramUsername: String? = "",
  @SerialName("last_name") val lastName: String? = "",
  @SerialName("links") val links: LinksX? = LinksX(),
  @SerialName("location") val location: String? = "",
  @SerialName("name") val name: String? = "",
  @SerialName("portfolio_url") val portfolioUrl: String? = "",
  @SerialName("profile_image") val profileImage: ProfileImage? = ProfileImage(),
  @SerialName("social") val social: Social? = Social(),
  @SerialName("total_collections") val totalCollections: Int? = 0,
  @SerialName("total_likes") val totalLikes: Int? = 0,
  @SerialName("total_photos") val totalPhotos: Int? = 0,
  @SerialName("twitter_username") val twitterUsername: String? = "",
  @SerialName("updated_at") val updatedAt: String? = "",
  @SerialName("username") val username: String? = ""
) : JavaSerializable {
  override fun toString(): String {
    return Json.encodeToString(this)
  }
}
