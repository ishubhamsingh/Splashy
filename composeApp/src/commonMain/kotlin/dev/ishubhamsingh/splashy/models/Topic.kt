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

@Serializable
data class Topic(
  @SerialName("cover_photo") val coverPhoto: CoverPhoto? = null,
  @SerialName("description") val description: String? = null,
  @SerialName("ends_at") val endsAt: String? = null,
  @SerialName("featured") val featured: Boolean? = null,
  @SerialName("id") val id: String? = null,
  @SerialName("links") val links: Links? = null,
  @SerialName("only_submissions_after") val onlySubmissionsAfter: String? = null,
  @SerialName("owners") val owners: List<Owner?>? = null,
  @SerialName("published_at") val publishedAt: String? = null,
  @SerialName("slug") val slug: String? = null,
  @SerialName("starts_at") val startsAt: String? = null,
  @SerialName("status") val status: String? = null,
  @SerialName("title") val title: String? = null,
  @SerialName("total_photos") val totalPhotos: Int? = null,
  @SerialName("updated_at") val updatedAt: String? = null,
  @SerialName("visibility") val visibility: String? = null
) {
  @Serializable
  data class CoverPhoto(
    @SerialName("alt_description") val altDescription: String? = null,
    @SerialName("blur_hash") val blurHash: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("links") val links: Links? = null,
    @SerialName("preview_photos") val previewPhotos: List<PreviewPhoto?>? = null,
    @SerialName("promoted_at") val promotedAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("urls") val urls: Urls? = null,
    @SerialName("user") val user: User? = null,
    @SerialName("width") val width: Int? = null
  ) {
    @Serializable
    data class Links(
      @SerialName("download") val download: String? = null,
      @SerialName("download_location") val downloadLocation: String? = null,
      @SerialName("html") val html: String? = null,
      @SerialName("self") val self: String? = null
    )

    @Serializable
    data class PreviewPhoto(
      @SerialName("created_at") val createdAt: String? = null,
      @SerialName("id") val id: String? = null,
      @SerialName("updated_at") val updatedAt: String? = null,
      @SerialName("urls") val urls: Urls? = null
    ) {
      @Serializable
      data class Urls(
        @SerialName("full") val full: String? = null,
        @SerialName("raw") val raw: String? = null,
        @SerialName("regular") val regular: String? = null,
        @SerialName("small") val small: String? = null,
        @SerialName("thumb") val thumb: String? = null
      )
    }

    @Serializable
    data class Urls(
      @SerialName("full") val full: String? = null,
      @SerialName("raw") val raw: String? = null,
      @SerialName("regular") val regular: String? = null,
      @SerialName("small") val small: String? = null,
      @SerialName("thumb") val thumb: String? = null
    )

    @Serializable
    data class User(
      @SerialName("accepted_tos") val acceptedTos: Boolean? = null,
      @SerialName("bio") val bio: String? = null,
      @SerialName("first_name") val firstName: String? = null,
      @SerialName("id") val id: String? = null,
      @SerialName("instagram_username") val instagramUsername: String? = null,
      @SerialName("last_name") val lastName: String? = null,
      @SerialName("links") val links: Links? = null,
      @SerialName("location") val location: String? = null,
      @SerialName("name") val name: String? = null,
      @SerialName("portfolio_url") val portfolioUrl: String? = null,
      @SerialName("profile_image") val profileImage: ProfileImage? = null,
      @SerialName("total_collections") val totalCollections: Int? = null,
      @SerialName("total_likes") val totalLikes: Int? = null,
      @SerialName("total_photos") val totalPhotos: Int? = null,
      @SerialName("twitter_username") val twitterUsername: String? = null,
      @SerialName("updated_at") val updatedAt: String? = null,
      @SerialName("username") val username: String? = null
    ) {
      @Serializable
      data class Links(
        @SerialName("followers") val followers: String? = null,
        @SerialName("following") val following: String? = null,
        @SerialName("html") val html: String? = null,
        @SerialName("likes") val likes: String? = null,
        @SerialName("photos") val photos: String? = null,
        @SerialName("portfolio") val portfolio: String? = null,
        @SerialName("self") val self: String? = null
      )

      @Serializable
      data class ProfileImage(
        @SerialName("large") val large: String? = null,
        @SerialName("medium") val medium: String? = null,
        @SerialName("small") val small: String? = null
      )
    }
  }

  @Serializable
  data class Links(
    @SerialName("html") val html: String? = null,
    @SerialName("photos") val photos: String? = null,
    @SerialName("self") val self: String? = null
  )

  @Serializable
  data class Owner(
    @SerialName("accepted_tos") val acceptedTos: Boolean? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("instagram_username") val instagramUsername: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("links") val links: Links? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("portfolio_url") val portfolioUrl: String? = null,
    @SerialName("profile_image") val profileImage: ProfileImage? = null,
    @SerialName("total_collections") val totalCollections: Int? = null,
    @SerialName("total_likes") val totalLikes: Int? = null,
    @SerialName("total_photos") val totalPhotos: Int? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("username") val username: String? = null
  ) {
    @Serializable
    data class Links(
      @SerialName("followers") val followers: String? = null,
      @SerialName("following") val following: String? = null,
      @SerialName("html") val html: String? = null,
      @SerialName("likes") val likes: String? = null,
      @SerialName("photos") val photos: String? = null,
      @SerialName("portfolio") val portfolio: String? = null,
      @SerialName("self") val self: String? = null
    )

    @Serializable
    data class ProfileImage(
      @SerialName("large") val large: String? = null,
      @SerialName("medium") val medium: String? = null,
      @SerialName("small") val small: String? = null
    )
  }
}
