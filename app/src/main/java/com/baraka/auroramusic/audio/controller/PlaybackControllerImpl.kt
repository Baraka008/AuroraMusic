package com.baraka.auroramusic.audio.controller

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.baraka.auroramusic.data.dao.ListeningEventDao
import com.baraka.auroramusic.data.entities.EventType
import com.baraka.auroramusic.data.entities.ListeningEvent
import com.baraka.auroramusic.data.entities.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackControllerImpl @Inject constructor(
    private val player: ExoPlayer,
    private val eventDao: ListeningEventDao
) : PlaybackController {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun play(song: Song) {
        player.setMediaItem(MediaItem.fromUri(song.uri))
        player.prepare()
        player.play()
        recordEvent(song.id, EventType.PLAY_STARTED)
    }

    override fun pause() {
        player.pause()
    }

    override fun resume() {
        player.play()
    }

    override fun skip() {
        player.seekToNext()
        // Ideally we'd know which song was skipped
    }

    override fun previous() {
        player.seekToPrevious()
    }

    override fun setQueue(songs: List<Song>) {
        val items = songs.map { MediaItem.fromUri(it.uri) }
        player.setMediaItems(items)
        player.prepare()
    }

    override fun addToQueue(song: Song) {
        player.addMediaItem(MediaItem.fromUri(song.uri))
        recordEvent(song.id, EventType.QUEUED)
    }

    override fun clearQueue() {
        player.clearMediaItems()
    }

    private fun recordEvent(songId: Long, type: EventType) {
        scope.launch {
            eventDao.insertEvent(ListeningEvent(songId = songId, eventType = type))
        }
    }
}
