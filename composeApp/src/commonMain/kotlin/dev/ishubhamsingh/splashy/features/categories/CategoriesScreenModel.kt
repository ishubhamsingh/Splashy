/*
 * Copyright 2023 Shubham Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ishubhamsingh.splashy.features.categories

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.ishubhamsingh.splashy.core.domain.NetworkResult
import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.features.categories.ui.CategoriesEvent
import dev.ishubhamsingh.splashy.features.categories.ui.CategoriesState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Created by Shubham Singh on 30/08/23. */
class CategoriesScreenModel(
  private val unsplashRepository: UnsplashRepository,
  val settingsUtils: SettingsUtils
) : ScreenModel {

  private val _state = MutableStateFlow(CategoriesState())
  val state = _state.asStateFlow()

  init {
    onEvent(CategoriesEvent.Load)
  }

  fun onEvent(event: CategoriesEvent) {
    when (event) {
      CategoriesEvent.Refresh,
      CategoriesEvent.Load -> {
        fetchTopics()
        fetchCollections()
      }
    }
  }

  private fun fetchCollections() {
    screenModelScope.launch {
      unsplashRepository.getCollections(1).collect { networkResult ->
        when (networkResult) {
          is NetworkResult.Error -> {
            networkResult.message?.let {
              _state.update { categoriesState -> categoriesState.copy(networkError = it) }
            }
            Napier.e(networkResult.message.toString())
          }
          is NetworkResult.Loading -> {
            _state.update { detailsState ->
              detailsState.copy(isCollectionsLoading = networkResult.isLoading)
            }
          }
          is NetworkResult.Success -> {
            networkResult.data?.let {
              _state.update { categoriesState -> categoriesState.copy(collections = it) }
            }
          }
        }
      }
    }
  }

  private fun fetchTopics() {
    screenModelScope.launch {
      unsplashRepository.getTopics(1).collect { networkResult ->
        when (networkResult) {
          is NetworkResult.Error -> {
            networkResult.message?.let {
              _state.update { categoriesState -> categoriesState.copy(networkError = it) }
            }
            Napier.e(networkResult.message.toString())
          }
          is NetworkResult.Loading -> {
            _state.update { detailsState ->
              detailsState.copy(isTopicsLoading = networkResult.isLoading)
            }
          }
          is NetworkResult.Success -> {
            networkResult.data?.let {
              _state.update { categoriesState -> categoriesState.copy(topics = it) }
            }
          }
        }
      }
    }
  }
}
