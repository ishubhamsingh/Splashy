package dev.ishubhamsingh.splashy.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Social(
    @SerialName("instagram_username")
    val instagramUsername: String? = "",
    @SerialName("paypal_email")
    val paypalEmail: String? = null,
    @SerialName("portfolio_url")
    val portfolioUrl: String? = null,
    @SerialName("twitter_username")
    val twitterUsername: String? = null
)