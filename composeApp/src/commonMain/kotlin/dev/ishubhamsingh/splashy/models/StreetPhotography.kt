package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreetPhotography(
    @SerialName("approved_on")
    val approvedOn: String = "",
    @SerialName("status")
    val status: String = ""
)