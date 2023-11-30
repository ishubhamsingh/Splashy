package dev.ishubhamsingh.splashy.core.utils

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import dev.ishubhamsingh.splashy.features.settings.Theme


/**
 * Created by Shubham Singh on 24/11/23.
 */

interface KeyValueStorage {
    fun storeInt(key : String, value : Int)
    fun storeBoolean(key : String, value : Boolean)
    fun storeFloat(key : String, value : Float)
    fun storeString(key : String, value : String)
    fun fetchString(key : String,  default : String = "") : String
    fun fetchBoolean(key : String, default: Boolean) : Boolean
    fun fetchInt(key : String, default : Int) : Int
    fun fetchFloat(key : String, default : Float) : Float
    fun clearStorage()
    fun removeKey(key: String)
}

class SettingsUtils(private val settings: Settings = Settings()): KeyValueStorage {

    fun addListener(
        mSettings: ObservableSettings = settings as ObservableSettings,
        key: String,
        defaultValue: Int,
        callback: (Int?) -> Unit
    ) {
        mSettings.addIntListener(key, defaultValue) {newValue ->
            callback(newValue)
        }
    }

    override fun storeInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    override fun storeBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    override fun storeFloat(key: String, value: Float) {
        settings.putFloat(key, value)
    }

    override fun storeString(key: String, value: String) {
        settings.putString(key, value)
    }

    override fun fetchString(key: String, default: String): String {
        return settings.getString(key, default)
    }

    override fun fetchBoolean(key: String, default: Boolean): Boolean {
        return settings.getBoolean(key, default)
    }

    override fun fetchInt(key: String, default: Int): Int {
        return settings.getInt(key, default)
    }

    override fun fetchFloat(key: String, default: Float): Float {
        return settings.getFloat(key, default)
    }

    override fun clearStorage() {
        settings.clear()
    }

    override fun removeKey(key: String) {
        settings.remove(key)
    }

    companion object {
        const val THEME = "theme"
    }

    fun isDarkTheme(isSystemDarkTheme: Boolean): Boolean {
        val currentTheme = fetchInt(THEME, Theme.SYSTEM.value)

        return if (currentTheme == Theme.SYSTEM.value) {
            isSystemDarkTheme
        } else {
            currentTheme == Theme.DARK.value
        }
    }

}