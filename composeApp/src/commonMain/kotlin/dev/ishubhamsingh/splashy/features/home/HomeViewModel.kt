package dev.ishubhamsingh.splashy.features.home

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.features.home.ui.HomeEvent
import dev.ishubhamsingh.splashy.features.home.ui.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {
    private val unsplashRepository: UnsplashRepository by inject()

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        fetchPhotos()
    }

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.Load -> {
                fetchPhotos()
            }

            is HomeEvent.Refresh -> {
                fetchPhotos()
            }

            is HomeEvent.OnSearchQueryChange -> {

            }
        }
    }

    private fun nextPage() {
        _state.update {
            it.copy(currentPage = it.currentPage + 1)
        }
    }

    private fun resetPage() {
        _state.update {
            it.copy(currentPage = 0)
        }
    }

    fun fetchPhotos(page: Int = state.value.currentPage) {
        viewModelScope.launch {
            unsplashRepository.searchPhotos("wallpaper",page).collect { networkResult ->
                when(networkResult) {
                    is NetworkResult.Success -> {
                        networkResult.data?.let {resp ->
                            _state.update { homeState ->
                                homeState.copy(photos = resp.results)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        _state.update { homeState ->
                            homeState.copy(networkError = networkResult.message)
                        }
                    }

                    is NetworkResult.Loading -> {
                        _state.update { homeState ->
                            homeState.copy(isLoading = networkResult.isLoading)
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _state.update {
            HomeState()
        }
    }
}