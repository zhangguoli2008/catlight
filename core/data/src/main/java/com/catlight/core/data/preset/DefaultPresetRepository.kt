package com.catlight.core.data.preset

import com.catlight.core.domain.model.PresetColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DefaultPresetRepository : PresetRepository {

    private val presetFlow = MutableStateFlow(
        listOf(
            PresetColor("soft_purple", "磨皮感", "#7D3AF2", "柔肤紫调", 0),
            PresetColor("cool_white", "冷白皮", "#E9FBFF", "偏冷高亮", 1),
            PresetColor("pink_skin", "少女感", "#FFD7E3", "粉白柔和", 2),
            PresetColor("pure_white", "百搭光", "#FFFFFF", "纯白高亮", 3),
            PresetColor("chick_yellow", "小鸡黄", "#FFE066", "暖黄活力", 4),
            PresetColor("fresh_blue", "清新光", "#5DB3FF", "干净蓝调", 5),
            PresetColor("cyber_purple", "网感紫", "#5B22FF", "饱和紫蓝", 6),
            PresetColor("sunset", "落日灯", "#FF7A2F", "橙红氛围", 7)
        ).sortedBy { it.order }
    )

    override val presets: Flow<List<PresetColor>> = presetFlow

    override suspend fun presetByKey(key: String): PresetColor? =
        presetFlow.value.firstOrNull { it.key == key }
}
