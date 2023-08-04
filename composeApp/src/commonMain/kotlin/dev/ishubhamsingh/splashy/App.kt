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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Menu2
import compose.icons.evaicons.outline.Person
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
      topBar = {
        when (currentRoute(navigator)) {
          Screen.Home.route,
          Screen.Collections.route,
          Screen.Favourites.route -> {
            AppTopBar(navigator)
          }
        }
      },
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
            (if (mIsSelected) it.selectedNavIcon else it.unselectedNavIcon)?.let { navIcon ->
              Icon(imageVector = navIcon, contentDescription = it.title)
            }
          }
        },
        selectedContentColor = MaterialTheme.colorScheme.secondary,
        unselectedContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
        onClick = { navigator.navigate(it.route, NavOptions(launchSingleTop = true)) }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navigator: Navigator) {
  CenterAlignedTopAppBar(
    title = {
      Text("SPLASHY", fontSize = 24.sp, fontWeight = FontWeight.Medium, letterSpacing = 4.sp)
    },
    colors =
      TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.secondary,
        actionIconContentColor = MaterialTheme.colorScheme.secondary,
        navigationIconContentColor = MaterialTheme.colorScheme.secondary
      ),
    actions = {
      Icon(
        imageVector = EvaIcons.Outline.Menu2,
        contentDescription = "settings",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.clickable { navigator.navigate(Screen.Settings.route) }
      )
    },
    navigationIcon = {
      Icon(
        imageVector = EvaIcons.Outline.Person,
        contentDescription = "profile",
        tint = MaterialTheme.colorScheme.secondary
      )
    },
    modifier = Modifier.padding(horizontal = 8.dp)
  )
}
