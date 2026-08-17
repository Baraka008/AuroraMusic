package com.baraka.auroramusic.audio.controller

import com.baraka.auroramusic.data.entities.Song

import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val currentSong: StateFlow<Song?>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val currentBpm: StateFlow<Float>
    val currentEnergy: StateFlow<Float>
    
    fun play(song: Song, context: List<Song> = emptyList())
    fun pause()
    fun resume()
    fun skip()
    fun previous()
    fun seekTo(position: Long)
    fun playNext(song: Song)
    fun setPlaybackSpeed(speed: Float)
    fun setPlaybackPitch(pitch: Float)
    fun setSleepTimer(durationMinutes: Int)
    fun toggleFavorite(song: Song)
    fun setQueue(songs: List<Song>)
    fun addToQueue(song: Song)
    fun clearQueue()
}
