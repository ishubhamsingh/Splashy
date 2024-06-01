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
package dev.ishubhamsingh.splashy.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowIosBack
import dev.ishubhamsingh.splashy.core.utils.Platform
import dev.ishubhamsingh.splashy.core.utils.UpdateSystemBars
import dev.ishubhamsingh.splashy.core.utils.getPlatform
import dev.ishubhamsingh.splashy.core.utils.isChristmasNewYearWeek
import dev.ishubhamsingh.splashy.features.settings.SettingsScreenModel
import dev.ishubhamsingh.splashy.features.settings.Theme
import dev.ishubhamsingh.splashy.goBack
import dev.ishubhamsingh.splashy.isDarkThemeState
import dev.ishubhamsingh.splashy.resources.Res
import dev.ishubhamsingh.splashy.resources.lbl_back
import dev.ishubhamsingh.splashy.resources.lbl_material_you_desc
import dev.ishubhamsingh.splashy.resources.lbl_material_you_title
import dev.ishubhamsingh.splashy.resources.lbl_select_theme
import dev.ishubhamsingh.splashy.resources.lbl_settings
import dev.ishubhamsingh.splashy.resources.themes_array
import dev.ishubhamsingh.splashy.ui.components.GoBack
import dev.ishubhamsingh.splashy.ui.components.SnowFallComponent
import dev.ishubhamsingh.splashy.ui.theme.getLatoBold
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

class SettingsScreen : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val screenModel = getScreenModel<SettingsScreenModel>()
    UpdateSystemBars(
      statusBarColor = Color.Transparent,
      navigationBarColor = Color.Transparent,
      isDarkTheme = isDarkThemeState.value
    )

    val navigator = LocalNavigator.currentOrThrow
    val state = screenModel.state.collectAsState()
    Scaffold(
      topBar = {
        Box {
          TopAppBar(
            title = {
              Text(
                text = stringResource(Res.string.lbl_settings),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            },
            navigationIcon = {
              Icon(
                imageVector = EvaIcons.Outline.ArrowIosBack,
                contentDescription = stringResource(Res.string.lbl_back),
                modifier = Modifier.clickable { navigator.pop() }
              )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
          )
          if (isChristmasNewYearWeek()) {
            SnowFallComponent()
          }
        }
      }
    ) { paddingValues ->
      Surface(modifier = Modifier.padding(paddingValues = paddingValues)) {
        Column(modifier = Modifier.fillMaxSize()) {
          ThemeSettings(
            selectedTheme = state.value.selectedTheme,
            onShowThemeSelectionDialog = {
              screenModel.onEvent(SettingsEvent.ShowThemeSelectionDialog)
            }
          )
          if (getPlatform() == Platform.Android) {
            MaterialYouSettings(isMaterialYouEnabled = state.value.isMaterialYouEnabled) {
              screenModel.onEvent(SettingsEvent.OnMaterialYouToggle(it))
            }
          }
        }
      }
    }

    if (state.value.showThemeSelectionDialog) {
      ThemeSelectionDialog(
        selectedTheme = state.value.selectedTheme,
        onThemeSelected = { screenModel.onEvent(SettingsEvent.OnThemeChange(it)) },
        onDismiss = { screenModel.onEvent(SettingsEvent.HideThemeSelectionDialog) }
      )
    }

    if (goBack.value) {
      GoBack(navigator)
      goBack.value = false
    }
  }

  @Composable
  fun ThemeSettings(selectedTheme: Int, onShowThemeSelectionDialog: () -> Unit) {
    val themesArray = stringArrayResource(Res.array.themes_array)
    val themeSubTitle =
      when (selectedTheme) {
        Theme.SYSTEM.value -> themesArray[0]
        Theme.LIGHT.value -> themesArray[1]
        Theme.DARK.value -> themesArray[2]
        else -> themesArray[0]
      }
    Column(
      modifier = Modifier.padding(16.dp).fillMaxWidth().clickable { onShowThemeSelectionDialog() },
      verticalArrangement = Arrangement.spacedBy(4.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = stringResource(Res.string.lbl_select_theme),
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = themeSubTitle,
        style =
          MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
      )
    }
  }

  @Composable
  fun MaterialYouSettings(isMaterialYouEnabled: Boolean, onToggleMaterialYou: (Boolean) -> Unit) {
    Row(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
      ) {
        Text(
          text = stringResource(Res.string.lbl_material_you_title),
          style = MaterialTheme.typography.titleLarge
        )
        Text(
          text = stringResource(Res.string.lbl_material_you_desc),
          style =
            MaterialTheme.typography.titleMedium.copy(
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
      }
      Switch(checked = isMaterialYouEnabled, onCheckedChange = onToggleMaterialYou)
    }
  }

  @Composable
  fun ThemeSelectionDialog(
    selectedTheme: Int,
    onThemeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
  ) {
    Dialog(onDismissRequest = onDismiss) {
      ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
          )
      ) {
        Column(
          modifier = Modifier.padding(16.dp).fillMaxWidth(),
        ) {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.lbl_select_theme),
            fontSize = 24.sp,
            fontFamily = getLatoBold(),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.size(8.dp))
          Theme.entries.forEach { theme ->
            ThemeSelectionItem(
              theme = theme,
              isSelected = selectedTheme == theme.value,
              onThemeSelected = {
                onThemeSelected(theme.value)
                onDismiss()
              }
            )
          }
        }
      }
    }
  }

  @Composable
  fun ThemeSelectionItem(theme: Theme, isSelected: Boolean, onThemeSelected: () -> Unit) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
    ) {
      RadioButton(
        selected = isSelected,
        onClick = onThemeSelected,
      )
      Text(
        text = theme.name.lowercase().replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.titleMedium,
      )
    }
  }
}
