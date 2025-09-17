package com.catlight.core.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.catlight.core.domain.color.sanitizeHex
import com.catlight.core.domain.model.FillPanelMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATA_STORE_NAME = "fill_preferences"
private const val MAX_RECENT_COLORS = 6

private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)

class DataStoreFillPreferencesRepository(
    context: Context
) : FillPreferencesRepository {

    private val dataStore = context.dataStore

    override val selectedColorHex: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.SELECTED_COLOR_HEX]?.let(::sanitizeHex) ?: DEFAULT_COLOR
    }

    override val colorBrightness: Flow<Float> = dataStore.data.map { preferences ->
        preferences[Keys.COLOR_BRIGHTNESS]?.coerceIn(0.1f, 1f) ?: 1f
    }

    override val screenBrightness: Flow<Float> = dataStore.data.map { preferences ->
        preferences[Keys.SCREEN_BRIGHTNESS]?.coerceIn(0.1f, 1f) ?: 1f
    }

    override val panelMode: Flow<FillPanelMode> = dataStore.data.map { preferences ->
        preferences[Keys.PANEL_MODE]?.let { stored ->
            runCatching { FillPanelMode.valueOf(stored) }.getOrNull()
        } ?: FillPanelMode.PRESETS
    }

    override val isUiLocked: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.UI_LOCKED] ?: false
    }

    override val recentColors: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[Keys.RECENT_COLORS]?.split(',')
            ?.mapNotNull { value -> value.takeIf { it.isNotBlank() }?.let(::sanitizeHex) }
            ?.distinct()
            ?.take(MAX_RECENT_COLORS)
            ?: emptyList()
    }

    override suspend fun setSelectedColorHex(hex: String) {
        val sanitized = sanitizeHex(hex)
        dataStore.edit { preferences ->
            preferences[Keys.SELECTED_COLOR_HEX] = sanitized
        }
        addRecentColor(sanitized)
    }

    override suspend fun setColorBrightness(value: Float) {
        dataStore.edit { preferences ->
            preferences[Keys.COLOR_BRIGHTNESS] = value.coerceIn(0.1f, 1f)
        }
    }

    override suspend fun setScreenBrightness(value: Float) {
        dataStore.edit { preferences ->
            preferences[Keys.SCREEN_BRIGHTNESS] = value.coerceIn(0.1f, 1f)
        }
    }

    override suspend fun setPanelMode(mode: FillPanelMode) {
        dataStore.edit { preferences ->
            preferences[Keys.PANEL_MODE] = mode.name
        }
    }

    override suspend fun setUiLocked(locked: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.UI_LOCKED] = locked
        }
    }

    override suspend fun addRecentColor(hex: String) {
        val sanitized = sanitizeHex(hex)
        dataStore.edit { preferences ->
            val current = preferences[Keys.RECENT_COLORS]
                ?.split(',')
                ?.filter { it.isNotBlank() && it != sanitized }
                ?.toMutableList()
                ?: mutableListOf()
            current.add(0, sanitized)
            val trimmed = current.distinct().take(MAX_RECENT_COLORS)
            preferences[Keys.RECENT_COLORS] = trimmed.joinToString(",")
        }
    }

    override suspend fun removeRecentColor(hex: String) {
        val sanitized = sanitizeHex(hex)
        dataStore.edit { preferences ->
            val current = preferences[Keys.RECENT_COLORS]
                ?.split(',')
                ?.filter { it.isNotBlank() && it != sanitized }
                ?.take(MAX_RECENT_COLORS)
            if (current.isNullOrEmpty()) {
                preferences.remove(Keys.RECENT_COLORS)
            } else {
                preferences[Keys.RECENT_COLORS] = current.joinToString(",")
            }
        }
    }

    private object Keys {
        val SELECTED_COLOR_HEX = stringPreferencesKey("selected_color_hex")
        val COLOR_BRIGHTNESS = floatPreferencesKey("color_brightness")
        val SCREEN_BRIGHTNESS = floatPreferencesKey("screen_brightness")
        val PANEL_MODE = stringPreferencesKey("panel_mode")
        val UI_LOCKED = booleanPreferencesKey("ui_locked")
        val RECENT_COLORS = stringPreferencesKey("recent_colors")
    }

    private companion object {
        const val DEFAULT_COLOR = "#FFFFFF"
    }
}
