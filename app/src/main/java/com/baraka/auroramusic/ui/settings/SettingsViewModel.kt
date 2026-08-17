package com.baraka.auroramusic.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraka.auroramusic.audio.NativeAudioEngine
import com.baraka.auroramusic.data.SettingsRepository
import com.baraka.auroramusic.data.dao.DJSettingsDao
import com.baraka.auroramusic.data.entities.DJSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val djSettingsDao: DJSettingsDao,
    private val nativeEngine: NativeAudioEngine,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val djSettings: StateFlow<DJSettings?> = djSettingsDao.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val amoledMode: StateFlow<Boolean> = settingsRepository.amoledMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun updateAmoledMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAmoledMode(enabled)
        }
    }

    fun updateVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = djSettings.value ?: DJSettings()
            djSettingsDao.updateSettings(current.copy(voiceEnabled = enabled))
        }
    }

    fun updateCommentaryFrequency(frequency: String) {
        viewModelScope.launch {
            val current = djSettings.value ?: DJSettings()
            djSettingsDao.updateSettings(current.copy(commentaryFrequency = frequency))
        }
    }

    fun updateDiscoveryLevel(level: Float) {
        viewModelScope.launch {
            val current = djSettings.value ?: DJSettings()
            djSettingsDao.updateSettings(current.copy(discoveryLevel = level))
        }
    }

    fun setEQBand(bandIdx: Int, gainDb: Float) {
        nativeEngine.setEQBand(bandIdx, gainDb)
        // In a real app, we might also save these to a database
    }

    fun setEQPreset(presetIdx: Int) {
        nativeEngine.setEQPreset(presetIdx)
    }

    fun setBassBoost(gainDb: Float) {
        nativeEngine.setBassBoost(gainDb)
    }

    fun setReverbLevel(level: Float) {
        nativeEngine.setReverbLevel(level)
    }

    fun setVirtualizerLevel(level: Float) {
        nativeEngine.setVirtualizerLevel(level)
    }
}
