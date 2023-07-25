package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoSearchCollection(
    @SerialName("results")
    val results: ArrayList<Photo> = arrayListOf(),
    @SerialName("total")
    val total: Int = 0,
    @SerialName("total_pages")
    val totalPages: Int = 0
)