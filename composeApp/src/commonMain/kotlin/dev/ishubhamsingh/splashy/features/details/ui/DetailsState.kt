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
package dev.ishubhamsingh.splashy.features.details.ui

import dev.ishubhamsingh.splashy.models.Photo

data class DetailsState(
  val id: String? = null,
  val photo: Photo? = null,
  val isFavourite: Boolean = false,
  val isLoading: Boolean = false,
  val isDownloading: Boolean = false,
  val isApplying: Boolean = false,
  val networkError: String? = null,
  val snackBarMessage: String? = null,
  val shouldShowApplyWallpaperDialog: Boolean = false
)
