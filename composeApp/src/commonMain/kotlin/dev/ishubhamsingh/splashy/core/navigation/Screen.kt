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

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Folder
import compose.icons.evaicons.fill.Heart
import compose.icons.evaicons.fill.Home
import compose.icons.evaicons.outline.Folder
import compose.icons.evaicons.outline.Heart
import compose.icons.evaicons.outline.Home
import compose.icons.feathericons.Folder
import compose.icons.feathericons.Heart
import compose.icons.feathericons.Home

sealed class Screen(
  val route: String,
  val title: String,
  val selectedNavIcon: ImageVector,
  val unselectedNavIcon: ImageVector
) {
  object Home :
    Screen(
      route = "home",
      title = "Home",
      selectedNavIcon = EvaIcons.Fill.Home,
      unselectedNavIcon = EvaIcons.Outline.Home
    )

  object Collections :
    Screen(
      route = "collections",
      title = "Collections",
      selectedNavIcon = EvaIcons.Fill.Folder,
      unselectedNavIcon = EvaIcons.Outline.Folder
    )

  object Favourites :
    Screen(
      route = "favourites",
      title = "Favourites",
      selectedNavIcon = EvaIcons.Fill.Heart,
      unselectedNavIcon = EvaIcons.Outline.Heart
    )
}
