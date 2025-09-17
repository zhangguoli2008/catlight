package com.catlight.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.catlight.core.ui.theme.CatlightTheme
import com.catlight.feature.fill.FillRoute
import com.catlight.feature.fill.FillViewModel
import com.catlight.feature.fill.FillViewModelFactory

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer by lazy {
        (application as CatlightApplication).container
    }

    private val fillViewModel: FillViewModel by viewModels {
        FillViewModelFactory(
            appContainer.presetRepository,
            appContainer.fillPreferencesRepository
        )
    }

    private lateinit var brightnessController: BrightnessController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        brightnessController = BrightnessController(window)

        setContent {
            CatlightTheme {
                FillRoute(
                    viewModel = fillViewModel,
                    onCameraClick = { /* TODO: Implement camera entry in Pro version */ },
                    onScreenBrightnessChanged = brightnessController::setBrightness
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        brightnessController.restore()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
