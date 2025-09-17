package com.catlight.feature.fill

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catlight.feature.fill.ui.FillScreen

@Composable
fun FillRoute(
    viewModel: FillViewModel,
    onCameraClick: () -> Unit,
    onScreenBrightnessChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateReduceMotion(!ValueAnimator.areAnimatorsEnabled())
    }

    LaunchedEffect(state.screenBrightness) {
        onScreenBrightnessChanged(state.screenBrightness)
    }

    FillScreen(
        state = state,
        onPresetSelected = viewModel::selectPreset,
        onRecentSelected = viewModel::selectRecent,
        onRecentRemoved = viewModel::removeRecent,
        onColorBrightnessChange = viewModel::updateColorBrightness,
        onScreenBrightnessChange = viewModel::updateScreenBrightness,
        onPanelModeChange = viewModel::setPanelMode,
        onTogglePanel = viewModel::togglePanelVisibility,
        onRequestHexInput = viewModel::requestHexInput,
        onSubmitHex = viewModel::submitHex,
        onCancelHex = viewModel::cancelHexInput,
        onColorWheelChange = viewModel::updateColorFromWheel,
        onToggleLock = viewModel::setUiLocked,
        onDoubleTap = viewModel::setFullWhite,
        onCameraClick = onCameraClick,
        modifier = modifier
    )
}
