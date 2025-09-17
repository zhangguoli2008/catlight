package com.catlight.core.data.preset

import com.catlight.core.domain.model.PresetColor
import kotlinx.coroutines.flow.Flow

interface PresetRepository {
    val presets: Flow<List<PresetColor>>
    suspend fun presetByKey(key: String): PresetColor?
}
