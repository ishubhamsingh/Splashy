package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileImage(
    @SerialName("large")
    val large: String = "",
    @SerialName("medium")
    val medium: String = "",
    @SerialName("small")
    val small: String = ""
)