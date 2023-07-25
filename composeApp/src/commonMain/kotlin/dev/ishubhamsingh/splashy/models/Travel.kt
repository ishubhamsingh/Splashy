package dev.ishubhamsingh.splashy.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Travel(
    @SerialName("status")
    val status: String? = null,
    @SerialName("approved_on")
    val approvedOn: String? = null
)
