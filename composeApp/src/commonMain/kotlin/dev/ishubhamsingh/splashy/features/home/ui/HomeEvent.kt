package dev.ishubhamsingh.splashy.features.home.ui

sealed class HomeEvent{
   object Load: HomeEvent()
   object Refresh: HomeEvent()
   data class OnSearchQueryChange(val query: String): HomeEvent()
}
