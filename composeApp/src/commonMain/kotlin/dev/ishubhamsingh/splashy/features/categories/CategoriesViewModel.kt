package dev.ishubhamsingh.splashy.features.categories

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.features.categories.ui.CategoriesEvent
import dev.ishubhamsingh.splashy.features.categories.ui.CategoriesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Shubham Singh on 30/08/23.
 */
class CategoriesViewModel: ViewModel(), KoinComponent {
    private val unsplashRepository: UnsplashRepository by inject()

    private val _state = MutableStateFlow(CategoriesState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    init {
        onEvent(CategoriesEvent.Load)
    }

    fun onEvent(event: CategoriesEvent) {
        when(event) {
            CategoriesEvent.Load -> {
                fetchTopics()
                fetchCollections()
            }
            CategoriesEvent.Refresh -> TODO()
        }
    }


    private fun fetchCollections() {
        viewModelScope.launch {
            unsplashRepository.getCollections(1).collect {networkResult ->
                when(networkResult) {
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {
                        networkResult.data?.let {
                            _state.update { categoriesState ->  categoriesState.copy(collections = it)}
                        }
                    }
                }
            }
        }
    }

    private fun fetchTopics() {
        viewModelScope.launch {
            unsplashRepository.getTopics(1).collect {networkResult ->
                when(networkResult) {
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {
                        networkResult.data?.let {
                            _state.update { categoriesState ->  categoriesState.copy(topics = it)}
                        }
                    }
                }
            }
        }
    }
}