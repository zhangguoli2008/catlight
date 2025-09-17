package com.catlight.core.domain.color

import android.graphics.Color
import kotlin.math.roundToInt

private const val HEX_PREFIX = "#"

fun sanitizeHex(input: String): String {
    val value = input.trim().removePrefix(HEX_PREFIX)
    val normalized = when (value.length) {
        3 -> buildString {
            value.forEach { append(it).append(it) }
        }
        6 -> value
        8 -> value
        else -> value.takeLast(6).padStart(6, '0')
    }
    return "$HEX_PREFIX${normalized.uppercase()}"
}

fun hexToColorInt(hex: String): Int = Color.parseColor(sanitizeHex(hex))

fun hexToHsv(hex: String): FloatArray {
    val hsv = FloatArray(3)
    Color.colorToHSV(hexToColorInt(hex), hsv)
    return hsv
}

fun hsvToHex(hue: Float, saturation: Float, value: Float): String {
    val colorInt = Color.HSVToColor(floatArrayOf(hue, saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f)))
    return "#%06X".format(colorInt and 0xFFFFFF)
}

fun applyValueToHex(hex: String, value: Float): String {
    val hsv = hexToHsv(hex)
    hsv[2] = value.coerceIn(0f, 1f)
    return hsvToHex(hsv[0], hsv[1], hsv[2])
}

fun hexToArgb(hex: String, alpha: Float): Int {
    val colorInt = hexToColorInt(hex)
    val a = (alpha.coerceIn(0f, 1f) * 255).roundToInt()
    val r = Color.red(colorInt)
    val g = Color.green(colorInt)
    val b = Color.blue(colorInt)
    return Color.argb(a, r, g, b)
}
