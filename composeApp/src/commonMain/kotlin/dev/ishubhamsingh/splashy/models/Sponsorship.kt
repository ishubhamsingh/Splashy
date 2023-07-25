package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sponsorship(
    @SerialName("impression_urls")
    val impressionUrls: List<String> = listOf(),
    @SerialName("sponsor")
    val sponsor: Sponsor = Sponsor(),
    @SerialName("tagline")
    val tagline: String = "",
    @SerialName("tagline_url")
    val taglineUrl: String = ""
)