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
package dev.ishubhamsingh.splashy.features.settings

import cafe.adriel.voyager.core.model.ScreenModel
import dev.ishubhamsingh.splashy.core.utils.SettingsUtils
import dev.ishubhamsingh.splashy.features.settings.ui.SettingsEvent
import dev.ishubhamsingh.splashy.features.settings.ui.SettingsState
import dev.ishubhamsingh.splashy.isMaterialYouEnabledState
import dev.ishubhamsingh.splashy.selectedThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Created by Shubham Singh on 24/11/23. */
class SettingsScreenModel(val settingsUtils: SettingsUtils) : ScreenModel {

  private val _state = MutableStateFlow(SettingsState())
  val state = _state.asStateFlow()

  init {
    val currentTheme = settingsUtils.fetchInt(SettingsUtils.THEME, Theme.SYSTEM.value)
    val isMaterialYouEnabled =
      settingsUtils.fetchBoolean(SettingsUtils.IS_MATERIAL_YOU_ENABLED, false)

    _state.update {
      it.copy(selectedTheme = currentTheme, isMaterialYouEnabled = isMaterialYouEnabled)
    }
  }

  fun onEvent(event: SettingsEvent) {
    when (event) {
      is SettingsEvent.OnThemeChange -> {
        settingsUtils.storeInt(SettingsUtils.THEME, event.selectedTheme)
        _state.update {
          it.copy(
            selectedTheme = event.selectedTheme,
          )
        }
        selectedThemeState.value = event.selectedTheme
      }
      is SettingsEvent.OnMaterialYouToggle -> {
        settingsUtils.storeBoolean(
          SettingsUtils.IS_MATERIAL_YOU_ENABLED,
          event.isMaterialYouEnabled
        )
        _state.update {
          it.copy(
            isMaterialYouEnabled = event.isMaterialYouEnabled,
          )
        }
        isMaterialYouEnabledState.value = event.isMaterialYouEnabled
      }
      SettingsEvent.ShowThemeSelectionDialog -> {
        _state.update {
          it.copy(
            showThemeSelectionDialog = true,
          )
        }
      }
      SettingsEvent.HideThemeSelectionDialog -> {
        _state.update {
          it.copy(
            showThemeSelectionDialog = false,
          )
        }
      }
    }
  }
}

enum class Theme(val value: Int) {
  LIGHT(0),
  DARK(1),
  SYSTEM(2)
}
