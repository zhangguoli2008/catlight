package com.catlight.feature.fill.ui

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.catlight.feature.fill.model.ColorWheelState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ColorWheel(
    state: ColorWheelState,
    modifier: Modifier = Modifier,
    onColorChange: (hue: Float, saturation: Float) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val diameter = min(maxWidth, maxHeight)
        val density = LocalDensity.current
        val diameterPx = with(density) { diameter.toPx() }.roundToInt().coerceAtLeast(2)
        val wheelBitmap = remember(diameterPx) { generateColorWheelBitmap(diameterPx) }
        val radiusPx = diameterPx / 2f

        val indicator = remember(state.hue, state.saturation, diameterPx) {
            hueSaturationToOffset(state.hue, state.saturation, radiusPx)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(androidx.compose.ui.graphics.Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val (hue, saturation) = offsetToHueSaturation(offset, radiusPx)
                        onColorChange(hue, saturation)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = { offset ->
                        val (hue, saturation) = offsetToHueSaturation(offset, radiusPx)
                        onColorChange(hue, saturation)
                    }) { change, _ ->
                        change.consume()
                        val (hue, saturation) = offsetToHueSaturation(change.position, radiusPx)
                        onColorChange(hue, saturation)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = wheelBitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Canvas(modifier = Modifier.fillMaxWidth()) {
                val indicatorOffset = indicator
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.White,
                    radius = 8.dp.toPx(),
                    center = Offset(indicatorOffset.x + radiusPx, indicatorOffset.y + radiusPx),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                    radius = 12.dp.toPx(),
                    center = Offset(indicatorOffset.x + radiusPx, indicatorOffset.y + radiusPx)
                )
            }
        }
    }
}

private fun hueSaturationToOffset(hue: Float, saturation: Float, radius: Float): Offset {
    val angleRad = Math.toRadians(hue.toDouble())
    val distance = saturation.coerceIn(0f, 1f) * radius
    val x = (cos(angleRad) * distance).toFloat()
    val y = (sin(angleRad) * distance).toFloat()
    return Offset(x, y)
}

private fun offsetToHueSaturation(offset: Offset, radius: Float): Pair<Float, Float> {
    val x = offset.x - radius
    val y = offset.y - radius
    val distance = sqrt(x * x + y * y)
    val saturation = (distance / radius).coerceIn(0f, 1f)
    val hue = ((Math.toDegrees(atan2(y.toDouble(), x.toDouble())) + 360.0) % 360.0).toFloat()
    return hue to saturation
}

private fun generateColorWheelBitmap(size: Int): ImageBitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val center = size / 2f
    val radius = center
    for (y in 0 until size) {
        for (x in 0 until size) {
            val dx = x - center
            val dy = y - center
            val distance = sqrt(dx * dx + dy * dy)
            if (distance <= radius) {
                val saturation = (distance / radius).coerceIn(0f, 1f)
                val hue = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360.0) % 360.0).toFloat()
                val colorInt = Color.HSVToColor(floatArrayOf(hue, saturation, 1f))
                bitmap.setPixel(x, y, colorInt)
            } else {
                bitmap.setPixel(x, y, Color.TRANSPARENT)
            }
        }
    }
    return bitmap.asImageBitmap()
}
