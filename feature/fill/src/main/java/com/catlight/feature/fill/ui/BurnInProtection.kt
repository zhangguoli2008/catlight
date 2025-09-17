package com.catlight.feature.fill.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.asState
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

private const val DEFAULT_INTERVAL_MS = 45_000L
private val DEFAULT_MAX_OFFSET: Dp = 3.dp

@Composable
fun rememberBurnInOffset(
    enabled: Boolean,
    reduceMotionEnabled: Boolean,
    maxOffset: Dp = DEFAULT_MAX_OFFSET,
    intervalMs: Long = DEFAULT_INTERVAL_MS
): State<Offset> {
    val density = LocalDensity.current
    val maxOffsetPx = with(density) { maxOffset.toPx() }
    val animatable = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    LaunchedEffect(enabled, reduceMotionEnabled, maxOffsetPx, intervalMs) {
        if (!enabled || reduceMotionEnabled) {
            animatable.snapTo(Offset.Zero)
            return@LaunchedEffect
        }
        while (isActive) {
            val target = Offset(
                x = (Random.nextFloat() - 0.5f) * 2f * maxOffsetPx,
                y = (Random.nextFloat() - 0.5f) * 2f * maxOffsetPx
            )
            animatable.animateTo(target, animationSpec = tween(durationMillis = 1200))
            delay(intervalMs)
            animatable.animateTo(Offset.Zero, animationSpec = tween(durationMillis = 1200))
            delay(intervalMs)
        }
    }

    return animatable.asState()
}
