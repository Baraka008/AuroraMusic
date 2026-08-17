package com.baraka.auroramusic.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraka.auroramusic.audio.DeviceInfo
import com.baraka.auroramusic.audio.DeviceType
import com.baraka.auroramusic.audio.AudioDeviceManager
import com.baraka.auroramusic.audio.controller.PlaybackController
import com.baraka.auroramusic.data.LyricsRepository
import com.baraka.auroramusic.data.entities.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackController: PlaybackController,
    private val audioDeviceManager: AudioDeviceManager,
    private val lyricsRepository: LyricsRepository
) : ViewModel() {

    val currentSong: StateFlow<Song?> = playbackController.currentSong
    val isPlaying: StateFlow<Boolean> = playbackController.isPlaying
    val currentPosition: StateFlow<Long> = playbackController.currentPosition
    val duration: StateFlow<Long> = playbackController.duration
    val currentBpm: StateFlow<Float> = playbackController.currentBpm
    val currentEnergy: StateFlow<Float> = playbackController.currentEnergy
    
    val currentDevice: StateFlow<DeviceInfo> = audioDeviceManager.currentDevice

    private val _currentLyrics = MutableStateFlow<String?>(null)
    val currentLyrics: StateFlow<String?> = _currentLyrics.asStateFlow()

    private val _isFetchingLyrics = MutableStateFlow(false)
    val isFetchingLyrics: StateFlow<Boolean> = _isFetchingLyrics.asStateFlow()

    init {
        viewModelScope.launch {
            currentSong.collectLatest { song ->
                _currentLyrics.value = null
                if (song != null) {
                    fetchLyrics(song)
                }
            }
        }
    }

    private suspend fun fetchLyrics(song: Song) {
        _isFetchingLyrics.value = true
        _currentLyrics.value = lyricsRepository.getLyricsForSong(song)
        _isFetchingLyrics.value = false
    }

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled

    private val _repeatMode = MutableStateFlow(0) // 0: None, 1: All, 2: One
    val repeatMode: StateFlow<Int> = _repeatMode

    fun play(song: Song, context: List<Song> = emptyList()) {
        playbackController.play(song, context)
    }

    fun togglePlayback() {
        if (isPlaying.value) {
            playbackController.pause()
        } else {
            playbackController.resume()
        }
    }

    fun skip() {
        playbackController.skip()
    }

    fun previous() {
        playbackController.previous()
    }

    fun seekTo(position: Long) {
        playbackController.seekTo(position)
    }

    fun setPlaybackSpeed(speed: Float) {
        playbackController.setPlaybackSpeed(speed)
    }

    fun setPlaybackPitch(pitch: Float) {
        playbackController.setPlaybackPitch(pitch)
    }

    fun setSleepTimer(minutes: Int) {
        playbackController.setSleepTimer(minutes)
    }

    fun toggleFavorite() {
        currentSong.value?.let { song ->
            playbackController.toggleFavorite(song)
        }
    }

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun cycleRepeatMode() {
        _repeatMode.value = (_repeatMode.value + 1) % 3
    }
}
