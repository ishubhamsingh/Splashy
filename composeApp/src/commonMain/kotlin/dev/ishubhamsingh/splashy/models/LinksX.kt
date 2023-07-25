package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinksX(
    @SerialName("followers")
    val followers: String = "",
    @SerialName("following")
    val following: String = "",
    @SerialName("html")
    val html: String = "",
    @SerialName("likes")
    val likes: String = "",
    @SerialName("photos")
    val photos: String = "",
    @SerialName("portfolio")
    val portfolio: String = "",
    @SerialName("self")
    val self: String = ""
)