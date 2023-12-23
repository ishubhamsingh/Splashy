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
package dev.ishubhamsingh.splashy.core.di.components

import dev.ishubhamsingh.splashy.core.domain.UnsplashRepository
import dev.ishubhamsingh.splashy.core.network.UnsplashRepositoryImpl
import dev.ishubhamsingh.splashy.core.network.api.UnsplashApi
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.core.utils.getHttpClient
import dev.ishubhamsingh.splashy.db.createDatabase
import dev.ishubhamsingh.splashy.features.categories.CategoriesScreenModel
import dev.ishubhamsingh.splashy.features.categoriesPhotos.CategoriesPhotosScreenModel
import dev.ishubhamsingh.splashy.features.favourites.FavouritesScreenModel
import dev.ishubhamsingh.splashy.features.home.HomeScreenModel
import dev.ishubhamsingh.splashy.features.settings.SettingsScreenModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
  singleOf(::getHttpClient)
  single { UnsplashApi(get()) }
  single { createDatabase(get()) }
  factory<UnsplashRepository> { UnsplashRepositoryImpl(get(), get()) }
  factory { HomeScreenModel(get(), get()) }
  factory { FavouritesScreenModel(get(), get()) }
  factory { CategoriesScreenModel(get(), get()) }
  factory { CategoriesPhotosScreenModel(get(), get()) }
  factory { SettingsScreenModel(get()) }
  single { SettingsUtils() }
}
