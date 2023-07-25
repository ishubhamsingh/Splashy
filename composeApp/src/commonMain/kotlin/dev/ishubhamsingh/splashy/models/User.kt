package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("accepted_tos")
    val acceptedTos: Boolean? = false,
    @SerialName("bio")
    val bio: String? = "",
    @SerialName("first_name")
    val firstName: String? = "",
    @SerialName("for_hire")
    val forHire: Boolean? = false,
    @SerialName("id")
    val id: String? = "",
    @SerialName("instagram_username")
    val instagramUsername: String? = "",
    @SerialName("last_name")
    val lastName: String? = "",
    @SerialName("links")
    val links: LinksX? = LinksX(),
    @SerialName("location")
    val location: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("portfolio_url")
    val portfolioUrl: String? = "",
    @SerialName("profile_image")
    val profileImage: ProfileImage? = ProfileImage(),
    @SerialName("social")
    val social: Social? = Social(),
    @SerialName("total_collections")
    val totalCollections: Int? = 0,
    @SerialName("total_likes")
    val totalLikes: Int? = 0,
    @SerialName("total_photos")
    val totalPhotos: Int? = 0,
    @SerialName("twitter_username")
    val twitterUsername: String? = "",
    @SerialName("updated_at")
    val updatedAt: String? = "",
    @SerialName("username")
    val username: String? = ""
)