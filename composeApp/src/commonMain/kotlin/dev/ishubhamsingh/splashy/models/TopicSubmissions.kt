package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicSubmissions(
    @SerialName("architecture-interior")
    val architectureInterior: ArchitectureInterior? = null,
    @SerialName("experimental")
    val experimental: Experimental? = null,
    @SerialName("fashion-beauty")
    val fashionBeauty: FashionBeauty? = null,
    @SerialName("street-photography")
    val streetPhotography: StreetPhotography? = null,
    @SerialName("wallpapers")
    val wallpapers: Wallpapers? = null,
    @SerialName("travel")
    val travel: Travel? = null
)