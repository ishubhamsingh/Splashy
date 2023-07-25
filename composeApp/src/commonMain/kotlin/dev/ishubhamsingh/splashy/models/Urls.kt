package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Urls(
    @SerialName("full")
    val full: String = "",
    @SerialName("raw")
    val raw: String = "",
    @SerialName("regular")
    val regular: String = "",
    @SerialName("small")
    val small: String = "",
    @SerialName("small_s3")
    val smallS3: String = "",
    @SerialName("thumb")
    val thumb: String = ""
)