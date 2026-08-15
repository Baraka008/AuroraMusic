package com.baraka.auroramusic.audio.controller

import com.baraka.auroramusic.data.entities.Song

interface PlaybackController {
    fun play(song: Song)
    fun pause()
    fun resume()
    fun skip()
    fun previous()
    fun setQueue(songs: List<Song>)
    fun addToQueue(song: Song)
    fun clearQueue()
}
