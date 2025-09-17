package com.catlight.feature.fill.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.catlight.core.domain.model.FillPanelMode
import com.catlight.core.ui.components.ColorSwatch
import com.catlight.core.ui.components.LabeledSlider
import com.catlight.core.ui.util.fromHex
import com.catlight.feature.fill.model.FillUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillScreen(
    state: FillUiState,
    onPresetSelected: (String) -> Unit,
    onRecentSelected: (String) -> Unit,
    onRecentRemoved: (String) -> Unit,
    onColorBrightnessChange: (Float) -> Unit,
    onScreenBrightnessChange: (Float) -> Unit,
    onPanelModeChange: (FillPanelMode) -> Unit,
    onTogglePanel: () -> Unit
    onRequestHexInput: (Boolean) -> Unit,
    onSubmitHex: (String) -> Unit,
    onCancelHex: () -> Unit,
    onColorWheelChange: (Float, Float) -> Unit,
    onToggleLock: (Boolean) -> Unit,
    onDoubleTap: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = scaffoldState.bottomSheetState

    LaunchedEffect(state.isPanelVisible) {
        if (state.isPanelVisible) {
            sheetState.expand()
        } else {
            runCatching { sheetState.hide() }
        }
    }

    val burnInOffsetState = rememberBurnInOffset(enabled = true, reduceMotionEnabled = state.isReduceMotionEnabled)

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 80.dp,
        sheetSwipeEnabled = !state.isUiLocked,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle()
        },
        sheetContent = {
            FillControlsSheet(
                state = state,
                onPresetSelected = onPresetSelected,
                onRecentSelected = onRecentSelected,
                onRecentRemoved = onRecentRemoved,
                onColorBrightnessChange = onColorBrightnessChange,
                onScreenBrightnessChange = onScreenBrightnessChange,
                onPanelModeChange = onPanelModeChange,
                onRequestHexInput = onRequestHexInput,
                onColorWheelChange = onColorWheelChange
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(state.displayColor)
                .offset {
                    IntOffset(
                        burnInOffsetState.value.x.roundToInt(),
                        burnInOffsetState.value.y.roundToInt()
                    )
                }
                .fillGestures(
                    isLocked = state.isUiLocked,
                    onTap = onTogglePanel,
                    onDoubleTap = onDoubleTap,
                    onLongPress = { onToggleLock(!state.isUiLocked) }
                )
        ) {
            IconButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = "进入自拍模式")
            }

            if (state.isUiLocked) {
                FilledIconButton(
                    onClick = { onToggleLock(false) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = "解锁界面")
                }
            }
        }
    }

    if (state.showHexInput) {
        HexInputDialog(
            currentHex = state.currentColorHex,
            onDismiss = onCancelHex,
            onConfirm = onSubmitHex
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FillControlsSheet(
    state: FillUiState,
    onPresetSelected: (String) -> Unit,
    onRecentSelected: (String) -> Unit,
    onRecentRemoved: (String) -> Unit,
    onColorBrightnessChange: (Float) -> Unit,
    onScreenBrightnessChange: (Float) -> Unit,
    onPanelModeChange: (FillPanelMode) -> Unit,
    onRequestHexInput: (Boolean) -> Unit,
    onColorWheelChange: (Float, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(selectedTabIndex = state.panelMode.ordinal) {
            FillPanelMode.values().forEach { mode ->
                Tab(
                    selected = state.panelMode == mode,
                    onClick = { onPanelModeChange(mode) },
                    text = { Text(text = if (mode == FillPanelMode.PRESETS) "预设颜色" else "色轮") }
                )
            }
        }

        when (state.panelMode) {
            FillPanelMode.PRESETS -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.presets, key = { it.key }) { preset ->
                        ColorSwatch(
                            hex = preset.hex,
                            name = preset.name,
                            description = preset.description,
                            selected = state.selectedPresetKey == preset.key,
                            onClick = { onPresetSelected(preset.key) }
                        )
                    }
                }
            }

            FillPanelMode.COLOR_WHEEL -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ColorWheel(
                        state = state.colorWheelState,
                        modifier = Modifier.fillMaxWidth(),
                        onColorChange = onColorWheelChange
                    )
                    TextButton(onClick = { onRequestHexInput(true) }) {
                        Text("输入十六进制颜色")
                    }
                }
            }
        }

        if (state.recentColors.isNotEmpty()) {
            RecentColorsRow(
                colors = state.recentColors,
                onColorSelected = onRecentSelected,
                onColorRemoved = onRecentRemoved
            )
        }

        LabeledSlider(
            label = "颜色亮度",
            value = state.colorBrightness,
            onValueChange = onColorBrightnessChange,
            valueRange = 0.1f..1f
        )

        if (state.screenBrightness < 0.6f) {
            Text(
                text = "建议提升系统亮度以获得更佳补光效果",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        LabeledSlider(
            label = "屏幕亮度",
            value = state.screenBrightness,
            onValueChange = onScreenBrightnessChange,
            valueRange = 0.1f..1f,
            valueFormatter = { String.format("%.0f%%", it * 100) }
        )
    }
}

@Composable
private fun RecentColorsRow(
    colors: List<String>,
    onColorSelected: (String) -> Unit,
    onColorRemoved: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "最近使用", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(colors, key = { it }) { hex ->
                RecentColorChip(
                    hex = hex,
                    onClick = { onColorSelected(hex) },
                    onLongPress = { onColorRemoved(hex) }
                )
            }
        }
    }
}

@Composable
private fun RecentColorChip(
    hex: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color.fromHex(hex),
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .fillGestures(
                isLocked = false,
                onTap = onClick,
                onDoubleTap = onClick,
                onLongPress = onLongPress
            )
    ) {}
}

private fun Modifier.fillGestures(
    isLocked: Boolean,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongPress: () -> Unit
): Modifier = pointerInput(isLocked) {
    androidx.compose.foundation.gestures.detectTapGestures(
        onTap = { if (!isLocked) onTap() },
        onDoubleTap = { onDoubleTap() },
        onLongPress = { onLongPress() }
    )
}
