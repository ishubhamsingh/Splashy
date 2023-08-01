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
package dev.ishubhamsingh.splashy

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.ishubhamsingh.splashy.core.navigation.Navigation
import dev.ishubhamsingh.splashy.core.navigation.Screen
import dev.ishubhamsingh.splashy.core.navigation.currentRoute
import dev.ishubhamsingh.splashy.core.presentation.SplashyTheme
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(darkTheme: Boolean, dynamicColor: Boolean) {
  val navigator = rememberNavigator()

  SplashyTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
    Scaffold(
      bottomBar = {
        when (currentRoute(navigator)) {
          Screen.Home.route,
          Screen.Collections.route,
          Screen.Favourites.route -> {
            BottomNavigationComponent(navigator)
          }
        }
      }
    ) {
      Navigation(navigator, it)
    }
  }
}

@Composable
fun BottomNavigationComponent(navigator: Navigator) {
  BottomNavigation(
    backgroundColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
    elevation = 8.dp
  ) {
    val items = listOf(Screen.Home, Screen.Collections, Screen.Favourites)

    items.forEach {
      val isSelected = it.route == currentRoute(navigator)
      BottomNavigationItem(
        label = {},
        selected = isSelected,
        icon = {
          Crossfade(
            targetState = isSelected,
            animationSpec = tween(100, 20, FastOutSlowInEasing)
          ) { mIsSelected ->
            Icon(
              imageVector = if (mIsSelected) it.selectedNavIcon else it.unselectedNavIcon,
              contentDescription = it.title
            )
          }
        },
        selectedContentColor = MaterialTheme.colorScheme.onSurface,
        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        onClick = { navigator.navigate(it.route, NavOptions(launchSingleTop = true)) }
      )
    }
  }
}
