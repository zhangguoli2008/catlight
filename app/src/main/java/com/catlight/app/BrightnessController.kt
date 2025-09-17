package com.catlight.app

import android.view.Window

class BrightnessController(private val window: Window) {

    private val originalBrightness: Float = window.attributes.screenBrightness.takeIf { it >= 0f } ?: -1f

    fun setBrightness(value: Float) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = value.coerceIn(0.1f, 1f)
        window.attributes = layoutParams
    }

    fun restore() {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = originalBrightness
        window.attributes = layoutParams
    }
}
