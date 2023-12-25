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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Menu2
import compose.icons.evaicons.outline.Person
import dev.ishubhamsingh.splashy.core.presentation.CommonRes
import dev.ishubhamsingh.splashy.core.utils.isChristmasNewYearWeek
import dev.ishubhamsingh.splashy.features.categories.ui.CategoriesTab
import dev.ishubhamsingh.splashy.features.favourites.ui.FavouritesTab
import dev.ishubhamsingh.splashy.features.home.ui.HomeTab
import dev.ishubhamsingh.splashy.features.settings.ui.SettingsScreen
import dev.ishubhamsingh.splashy.ui.components.SnowFallComponent
import dev.ishubhamsingh.splashy.ui.components.getKamelConfig
import dev.ishubhamsingh.splashy.ui.theme.getLatoRegular
import dev.ishubhamsingh.splashy.ui.theme.getLobsterRegular
import io.kamel.image.config.LocalKamelConfig
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/** Created by Shubham Singh on 29/09/23. */
class TopLevelScreen : Screen {
  @Composable
  override fun Content() {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    TabNavigator(HomeTab) {
      CompositionLocalProvider(LocalKamelConfig provides getKamelConfig()) {
        Scaffold(
          topBar = { AppTopBar() },
          bottomBar = { BottomNavigationComponent() },
          snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) {
              Snackbar(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier = Modifier.padding(16.dp)
              ) {
                Text(text = it.visuals.message, fontFamily = getLatoRegular(), fontSize = 16.sp)
              }
            }
          },
          content = { paddingValues ->
            Surface(
              modifier = Modifier.padding(paddingValues),
              color = MaterialTheme.colorScheme.surface
            ) {
              CurrentTab()
            }
          }
        )
      }
    }
  }

  @Composable
  private fun BottomNavigationComponent() {
    NavigationBar(
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      val items = listOf(HomeTab, CategoriesTab, FavouritesTab)

      items.forEach { TabNavigationItem(it) }
    }
  }

  @Composable
  private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val isSelected = tabNavigator.current.key == tab.key

    NavigationBarItem(
      label = { Text(text = tab.options.title, fontFamily = getLatoRegular()) },
      selected = isSelected,
      icon = {
        Crossfade(targetState = isSelected) { mIsSelected ->
          if (mIsSelected) {
            tab.options.icon?.let { icon ->
              Icon(
                icon,
                contentDescription = tab.options.title,
                tint = MaterialTheme.colorScheme.secondary
              )
            }
          } else {
            tab.options.icon?.let { icon ->
              Icon(
                icon,
                contentDescription = tab.options.title,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
              )
            }
          }
        }
      },
      colors =
        NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.secondary,
          selectedTextColor = MaterialTheme.colorScheme.secondary,
          unselectedIconColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
          unselectedTextColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
          indicatorColor = MaterialTheme.colorScheme.secondaryContainer
        ),
      onClick = { tabNavigator.current = tab },
      alwaysShowLabel = true
    )
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
  @Composable
  private fun AppTopBar(navigator: Navigator? = LocalNavigator.currentOrThrow.parent) {
    Box {
      CenterAlignedTopAppBar(
        title = {
          Box {
            Text(
              CommonRes.app_name,
              fontFamily = getLobsterRegular(),
              fontSize = 32.sp,
              fontWeight = FontWeight.Normal
            )
            if (isChristmasNewYearWeek()) {
              Icon(
                painterResource("hat.png"),
                contentDescription = "cap",
                tint = Color.Unspecified,
                modifier =
                  Modifier.size(24.dp)
                    .padding(bottom = 4.dp, end = 4.dp)
                    .scale(scaleX = -1.0f, scaleY = 1.0f)
              )
            }
          }
        },
        colors =
          TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
            navigationIconContentColor = MaterialTheme.colorScheme.secondary
          ),
        actions = {
          Icon(
            imageVector = EvaIcons.Outline.Menu2,
            contentDescription = "settings",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { navigator?.push(SettingsScreen()) }
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
      if (isChristmasNewYearWeek()) {
        SnowFallComponent()
      }
    }
  }
}
