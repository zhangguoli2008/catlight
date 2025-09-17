package com.catlight.feature.fill

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catlight.core.data.preferences.FillPreferencesRepository
import com.catlight.core.data.preset.PresetRepository
import com.catlight.core.domain.color.applyValueToHex
import com.catlight.core.domain.color.hexToColorInt
import com.catlight.core.domain.color.sanitizeHex
import com.catlight.core.domain.model.FillPanelMode
import com.catlight.feature.fill.model.ColorWheelState
import com.catlight.feature.fill.model.FillUiState
import com.catlight.feature.fill.model.copyFromHex
import com.catlight.feature.fill.model.toHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FillViewModel(
    private val presetRepository: PresetRepository,
    private val preferencesRepository: FillPreferencesRepository
) : ViewModel() {

    private val panelVisible = MutableStateFlow(true)
    private val showHexInput = MutableStateFlow(false)
    private val reduceMotion = MutableStateFlow(false)
    private val colorWheelState = MutableStateFlow(ColorWheelState())

    init {
        viewModelScope.launch {
            preferencesRepository.selectedColorHex.collect { hex ->
                val sanitized = sanitizeHex(hex)
                colorWheelState.update { it.copyFromHex(sanitized) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.colorBrightness.collect { value ->
                colorWheelState.update { current -> current.copy(value = value) }
            }
        }
    }

    val uiState: StateFlow<FillUiState> = combine(
        presetRepository.presets,
        preferencesRepository.selectedColorHex,
        preferencesRepository.colorBrightness,
        preferencesRepository.screenBrightness,
        preferencesRepository.panelMode,
        preferencesRepository.recentColors,
        preferencesRepository.isUiLocked,
        panelVisible,
        showHexInput,
        reduceMotion,
        colorWheelState
    ) {
            presets,
            selectedHex,
            colorBrightness,
            screenBrightness,
            panelMode,
            recents,
            locked,
            isPanelVisible,
            showHex,
            reduceMotionEnabled,
            wheel
        ->
        val sanitizedHex = sanitizeHex(selectedHex)
        val selectedPreset = presets.firstOrNull { sanitizeHex(it.hex) == sanitizedHex }?.key
        val adjustedHex = applyValueToHex(sanitizedHex, colorBrightness)
        val displayColor = Color(hexToColorInt(adjustedHex))
        val wheelState = wheel.copy(value = colorBrightness)

        FillUiState(
            presets = presets,
            currentColorHex = sanitizedHex,
            displayColor = displayColor,
            colorBrightness = colorBrightness,
            screenBrightness = screenBrightness,
            panelMode = panelMode,
            selectedPresetKey = selectedPreset,
            recentColors = recents,
            isUiLocked = locked,
            isPanelVisible = isPanelVisible,
            showHexInput = showHex,
            colorWheelState = wheelState,
            isReduceMotionEnabled = reduceMotionEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FillUiState()
    )

    fun togglePanelVisibility() {
        if (uiState.value.isUiLocked) return
        panelVisible.update { !it }
    }


    fun requestHexInput(show: Boolean) {
        showHexInput.value = show
    }

    fun updateReduceMotion(enabled: Boolean) {
        reduceMotion.value = enabled
    }

    fun setPanelMode(mode: FillPanelMode) {
        viewModelScope.launch {
            preferencesRepository.setPanelMode(mode)
        }
    }

    fun setUiLocked(locked: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setUiLocked(locked)
            if (!locked) {
                panelVisible.value = true
            }
        }
    }

    fun updateColorBrightness(value: Float) {
        viewModelScope.launch {
            preferencesRepository.setColorBrightness(value)
        }
    }

    fun updateScreenBrightness(value: Float) {
        viewModelScope.launch {
            preferencesRepository.setScreenBrightness(value)
        }
    }

    fun selectPreset(key: String) {
        viewModelScope.launch {
            val preset = presetRepository.presetByKey(key) ?: return@launch
            preferencesRepository.setSelectedColorHex(preset.hex)
        }
    }

    fun selectRecent(hex: String) {
        viewModelScope.launch {
            preferencesRepository.setSelectedColorHex(hex)
        }
    }

    fun removeRecent(hex: String) {
        viewModelScope.launch {
            preferencesRepository.removeRecentColor(hex)
        }
    }

    fun updateColorFromWheel(hue: Float, saturation: Float) {
        colorWheelState.update { it.copy(hue = hue, saturation = saturation) }
        val sanitized = sanitizeHex(ColorWheelState(hue, saturation, 1f).toHex())
        viewModelScope.launch {
            preferencesRepository.setSelectedColorHex(sanitized)
        }
    }

    fun submitHex(hex: String) {
        viewModelScope.launch {
            val sanitized = sanitizeHex(hex)
            preferencesRepository.setSelectedColorHex(sanitized)
            showHexInput.value = false
        }
    }

    fun cancelHexInput() {
        showHexInput.value = false
    }

    fun setFullWhite() {
        selectRecent("#FFFFFF")
    }
}
