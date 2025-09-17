package com.catlight.core.data.preferences

import com.catlight.core.domain.model.FillPanelMode
import kotlinx.coroutines.flow.Flow

interface FillPreferencesRepository {
    val selectedColorHex: Flow<String>
    val colorBrightness: Flow<Float>
    val screenBrightness: Flow<Float>
    val panelMode: Flow<FillPanelMode>
    val isUiLocked: Flow<Boolean>
    val recentColors: Flow<List<String>>

    suspend fun setSelectedColorHex(hex: String)
    suspend fun setColorBrightness(value: Float)
    suspend fun setScreenBrightness(value: Float)
    suspend fun setPanelMode(mode: FillPanelMode)
    suspend fun setUiLocked(locked: Boolean)
    suspend fun addRecentColor(hex: String)
    suspend fun removeRecentColor(hex: String)
}
