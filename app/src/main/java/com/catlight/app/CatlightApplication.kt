package com.catlight.app

import android.app.Application
import com.catlight.core.data.preferences.DataStoreFillPreferencesRepository
import com.catlight.core.data.preferences.FillPreferencesRepository
import com.catlight.core.data.preset.DefaultPresetRepository
import com.catlight.core.data.preset.PresetRepository

class CatlightApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(application: Application) {
    val presetRepository: PresetRepository = DefaultPresetRepository()
    val fillPreferencesRepository: FillPreferencesRepository =
        DataStoreFillPreferencesRepository(application)
}
