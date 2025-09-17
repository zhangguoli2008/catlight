package com.catlight.feature.fill.model

import androidx.compose.ui.graphics.Color
import com.catlight.core.domain.model.FillPanelMode
import com.catlight.core.domain.model.PresetColor

data class FillUiState(
    val presets: List<PresetColor> = emptyList(),
    val currentColorHex: String = "#FFFFFF",
    val displayColor: Color = Color.White,
    val colorBrightness: Float = 1f,
    val screenBrightness: Float = 1f,
    val panelMode: FillPanelMode = FillPanelMode.PRESETS,
    val selectedPresetKey: String? = null,
    val recentColors: List<String> = emptyList(),
    val isUiLocked: Boolean = false,
    val isPanelVisible: Boolean = true,
    val showHexInput: Boolean = false,
    val colorWheelState: ColorWheelState = ColorWheelState(),
    val isReduceMotionEnabled: Boolean = false
)

val FillUiState.isPresetSelected: Boolean
    get() = selectedPresetKey != null
