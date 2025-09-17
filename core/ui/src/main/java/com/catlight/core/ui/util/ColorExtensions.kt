package com.catlight.core.ui.util

import androidx.compose.ui.graphics.Color

fun Color.Companion.fromHex(hex: String): Color {
    val cleaned = hex.trim().removePrefix("#")
    val value = when (cleaned.length) {
        3 -> cleaned.map { "$it$it" }.joinToString("")
        6 -> cleaned
        8 -> cleaned.substring(2)
        else -> cleaned.takeLast(6).padStart(6, '0')
    }
    val colorInt = value.toLong(16).toInt()
    return Color(0xFF000000.toInt() or colorInt)
}
