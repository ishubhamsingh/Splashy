package dev.ishubhamsingh.splashy.models

import kotlinx.serialization.Serializable

/**
 * Created by Shubham Singh on 12/08/23.
 */
@Serializable
data class Favourite(
    val id: String,
    val color: String?,
    val altDescription: String?,
    val description: String?,
    val topicSubmissions: TopicSubmissions?,
    val url: String?,
)