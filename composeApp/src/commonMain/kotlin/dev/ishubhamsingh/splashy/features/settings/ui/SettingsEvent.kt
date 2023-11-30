package dev.ishubhamsingh.splashy.features.settings.ui

import dev.ishubhamsingh.splashy.core.domain.ScreenEvent

/**
 * Created by Shubham Singh on 24/11/23.
 */
sealed class SettingsEvent: ScreenEvent {
    data class OnThemeChange(val isDarkTheme: Boolean): SettingsEvent()

}