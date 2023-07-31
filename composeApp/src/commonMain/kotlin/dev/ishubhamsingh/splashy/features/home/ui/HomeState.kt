package dev.ishubhamsingh.splashy.features.home.ui

import dev.ishubhamsingh.splashy.models.Photo

data class HomeState(
    val photos: ArrayList<Photo> = arrayListOf(),
    val networkError: String? = null,
    val isLoading: Boolean = false,
    val searchQuery: String? = null,
    val currentPage: Int = 0
)
