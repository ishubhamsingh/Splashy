package dev.ishubhamsingh.splashy.features.categories.ui

/**
 * Created by Shubham Singh on 30/08/23.
 */
sealed class CategoriesEvent {
    data object Refresh: CategoriesEvent()
    data object Load: CategoriesEvent()
}