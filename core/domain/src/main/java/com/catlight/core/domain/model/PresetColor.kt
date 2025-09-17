package com.catlight.core.domain.model

data class PresetColor(
    val key: String,
    val name: String,
    val hex: String,
    val description: String? = null,
    val order: Int = 0
)
