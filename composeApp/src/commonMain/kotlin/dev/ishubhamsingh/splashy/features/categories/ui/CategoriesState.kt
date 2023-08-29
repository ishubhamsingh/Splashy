package dev.ishubhamsingh.splashy.features.categories.ui

import dev.ishubhamsingh.splashy.models.CollectionItem
import dev.ishubhamsingh.splashy.models.Topic

/**
 * Created by Shubham Singh on 30/08/23.
 */
data class CategoriesState(
    val isLoading: Boolean = false,
    val collections: ArrayList<CollectionItem> = arrayListOf(),
    val topics: ArrayList<Topic> = arrayListOf()
)