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
package dev.ishubhamsingh.splashy.features.categoriesPhotos.ui

import dev.ishubhamsingh.splashy.models.CollectionItem
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.models.Topic

data class CategoriesPhotosState(
  val id: String? = null,
  val isRefreshing: Boolean = false,
  val photos: ArrayList<Photo> = arrayListOf(),
  val topic: Topic? = null,
  val collection: CollectionItem? = null,
  val networkError: String? = null,
  val isPaginating: Boolean = false,
  val currentPage: Int = 1,
  val totalPages: Int = 0,
  val categoryType: CategoryType? = null
)
