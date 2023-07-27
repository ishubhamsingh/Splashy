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
package dev.ishubhamsingh.splashy.core.di

import com.arkivanov.decompose.ComponentContext
import dev.ishubhamsingh.splashy.core.api.UnsplashApi
import dev.ishubhamsingh.splashy.core.di.components.CommonComponents
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class ApplicationComponent(@get:Provides val componentContext: ComponentContext) :
  CommonComponents {
  abstract val unsplashApiCreator: () -> UnsplashApi
}
