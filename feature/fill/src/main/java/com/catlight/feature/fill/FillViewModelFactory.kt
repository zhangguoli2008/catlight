package com.catlight.feature.fill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.catlight.core.data.preferences.FillPreferencesRepository
import com.catlight.core.data.preset.PresetRepository

class FillViewModelFactory(
    private val presetRepository: PresetRepository,
    private val preferencesRepository: FillPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FillViewModel::class.java)) {
            return FillViewModel(presetRepository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
