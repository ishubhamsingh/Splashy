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
package dev.ishubhamsingh.splashy.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dev.ishubhamsingh.splashy.features.collections.ui.CollectionsScreen
import dev.ishubhamsingh.splashy.features.favourites.ui.FavouritesScreen
import dev.ishubhamsingh.splashy.features.home.ui.HomeScreen
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun Navigation(navigator: Navigator, paddingValues: PaddingValues) {
  NavHost(
    navigator = navigator,
    initialRoute = Screen.Home.route,
    modifier = Modifier.padding(paddingValues)
  ) {
    scene(route = Screen.Home.route) { HomeScreen(navigator) }
    scene(route = Screen.Collections.route) { CollectionsScreen(navigator) }
    scene(route = Screen.Favourites.route) { FavouritesScreen(navigator) }
  }
}

@Composable
fun currentRoute(navigator: Navigator): String? {
  return navigator.currentEntry.collectAsState(null).value?.route?.route
}
