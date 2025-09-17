package com.catlight.feature.fill.model

data class ColorWheelState(
    val hue: Float = 0f,
    val saturation: Float = 0f,
    val value: Float = 1f
)

fun ColorWheelState.toHex(): String {
    val hueNormalized = (hue % 360 + 360) % 360
    val saturationClamped = saturation.coerceIn(0f, 1f)
    val valueClamped = value.coerceIn(0f, 1f)
    val colorInt = android.graphics.Color.HSVToColor(
        floatArrayOf(hueNormalized, saturationClamped, valueClamped)
    )
    return "#%06X".format(colorInt and 0xFFFFFF)
}

fun ColorWheelState.copyFromHex(hex: String): ColorWheelState {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(android.graphics.Color.parseColor(hex), hsv)
    return copy(hue = hsv[0], saturation = hsv[1], value = hsv[2])
}
