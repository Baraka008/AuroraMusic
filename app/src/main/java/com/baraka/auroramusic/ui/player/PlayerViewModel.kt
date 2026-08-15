package com.baraka.auroramusic.ui.player

import androidx.lifecycle.ViewModel
import com.baraka.auroramusic.audio.controller.PlaybackController
import com.baraka.auroramusic.data.entities.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackController: PlaybackController
) : ViewModel() {

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled

    private val _repeatMode = MutableStateFlow(0) // 0: None, 1: All, 2: One
    val repeatMode: StateFlow<Int> = _repeatMode

    fun play(song: Song) {
        _currentSong.value = song
        _isPlaying.value = true
        playbackController.play(song)
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            playbackController.pause()
            _isPlaying.value = false
        } else {
            playbackController.resume()
            _isPlaying.value = true
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
