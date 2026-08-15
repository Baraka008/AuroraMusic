package com.baraka.auroramusic.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraka.auroramusic.audio.controller.PlaybackController
import com.baraka.auroramusic.data.entities.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackController: PlaybackController
) : ViewModel() {

    val currentSong: StateFlow<Song?> = playbackController.currentSong
    val isPlaying: StateFlow<Boolean> = playbackController.isPlaying
    val currentPosition: StateFlow<Long> = playbackController.currentPosition
    val duration: StateFlow<Long> = playbackController.duration

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled

    private val _repeatMode = MutableStateFlow(0) // 0: None, 1: All, 2: One
    val repeatMode: StateFlow<Int> = _repeatMode

    fun play(song: Song) {
        playbackController.play(song)
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

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun cycleRepeatMode() {
        _repeatMode.value = (_repeatMode.value + 1) % 3
    }
}
