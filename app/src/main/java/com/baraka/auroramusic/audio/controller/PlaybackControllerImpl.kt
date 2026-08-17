package com.baraka.auroramusic.audio.controller

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import com.baraka.auroramusic.data.SettingsRepository
import com.baraka.auroramusic.data.dao.MusicFeaturesDao
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.dao.ListeningEventDao
import com.baraka.auroramusic.data.entities.EventType
import com.baraka.auroramusic.data.entities.ListeningEvent
import com.baraka.auroramusic.data.entities.Song
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackControllerImpl @Inject constructor(
    private val player: ExoPlayer,
    private val eventDao: ListeningEventDao,
    private val songDao: SongDao,
    private val featuresDao: MusicFeaturesDao,
    private val settingsRepository: SettingsRepository
) : PlaybackController {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentSong = MutableStateFlow<Song?>(null)
    override val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentBpm = MutableStateFlow(0f)
    override val currentBpm: StateFlow<Float> = _currentBpm.asStateFlow()

    private val _currentEnergy = MutableStateFlow(0f)
    override val currentEnergy: StateFlow<Float> = _currentEnergy.asStateFlow()

    init {
        scope.launch {
            val lastId = settingsRepository.lastSongId.first()
            val lastPos = settingsRepository.lastPosition.first()
            if (lastId != null) {
                val song = songDao.getSongById(lastId)
                if (song != null) {
                    val mediaItem = MediaItem.Builder()
                        .setUri(song.uri)
                        .setMediaId(song.id.toString())
                        .build()
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.seekTo(lastPos)
                    _currentSong.value = song
                    _currentPosition.value = lastPos
                }
            }
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val songId = mediaItem?.mediaId?.toLongOrNull()
                if (songId != null) {
                    scope.launch {
                        val song = songDao.getSongById(songId)
                        _currentSong.value = song
                        _duration.value = player.duration.coerceAtLeast(0L)
                        
                        // Fetch features
                        val features = featuresDao.getFeaturesForSong(songId)
                        _currentBpm.value = features?.tempo ?: 0f
                        _currentEnergy.value = features?.energy ?: 0f

                        if (song != null) {
                            recordEvent(song.id, EventType.PLAY_STARTED)
                        }
                    }
                } else {
                    _currentSong.value = null
                    _duration.value = 0L
                    _currentBpm.value = 0f
                    _currentEnergy.value = 0f
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration.coerceAtLeast(0L)
                }
                
                // Real Crossfade trigger logic (Experimental)
                if (playbackState == Player.STATE_BUFFERING) {
                    scope.launch { rampVolume(0f) }
                } else if (playbackState == Player.STATE_READY) {
                    scope.launch { rampVolume(1f) }
                }
            }
        })

        // Poll position
        scope.launch {
            while (isActive) {
                if (player.isPlaying) {
                    val pos = player.currentPosition
                    _currentPosition.value = pos
                    _currentSong.value?.let { song ->
                        settingsRepository.savePlaybackState(song.id, pos)
                    }
                }
                delay(1000)
            }
        }
    }

    override fun play(song: Song, context: List<Song>) {
        val mediaItems = if (context.isNotEmpty()) {
            context.map { it.toMediaItem() }
        } else {
            listOf(song.toMediaItem())
        }
        
        val startIndex = if (context.isNotEmpty()) {
            context.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        } else {
            0
        }

        scope.launch {
            if (player.isPlaying) {
                rampVolume(0f)
            }
            player.setMediaItems(mediaItems, startIndex, 0L)
            player.prepare()
            player.play()
            rampVolume(1f)
        }
    }

    private fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId(id.toString())
            .build()
    }

    private suspend fun rampVolume(target: Float) {
        val steps = 10
        val current = player.volume
        val delta = (target - current) / steps
        for (i in 1..steps) {
            player.volume = current + (delta * i)
            delay(50)
        }
    }

    override fun pause() {
        player.pause()
    }

    override fun resume() {
        player.play()
    }

    override fun skip() {
        player.seekToNext()
    }

    override fun previous() {
        player.seekToPrevious()
    }

    override fun playNext(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setUri(song.uri)
            .setMediaId(song.id.toString())
            .build()
        val nextIndex = if (player.mediaItemCount > 0) player.currentMediaItemIndex + 1 else 0
        player.addMediaItem(nextIndex, mediaItem)
        recordEvent(song.id, EventType.QUEUED)
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position
    }

    override fun setPlaybackSpeed(speed: Float) {
        val params = player.playbackParameters
        player.playbackParameters = PlaybackParameters(speed, params.pitch)
    }

    override fun setPlaybackPitch(pitch: Float) {
        val params = player.playbackParameters
        player.playbackParameters = PlaybackParameters(params.speed, pitch)
    }

    private var sleepTimerJob: Job? = null
    override fun setSleepTimer(durationMinutes: Int) {
        sleepTimerJob?.cancel()
        if (durationMinutes <= 0) return
        
        sleepTimerJob = scope.launch {
            delay(durationMinutes * 60 * 1000L)
            // Optional: Fade out
            pause()
        }
    }

    override fun toggleFavorite(song: Song) {
        scope.launch(Dispatchers.IO) {
            val updatedSong = song.copy(isFavorite = !song.isFavorite)
            songDao.updateSong(updatedSong)
            if (_currentSong.value?.id == song.id) {
                _currentSong.value = updatedSong
            }
        }
    }

    override fun setQueue(songs: List<Song>) {
        val items = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.uri)
                .setMediaId(song.id.toString())
                .build()
        }
        player.setMediaItems(items)
        player.prepare()
    }

    override fun addToQueue(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setUri(song.uri)
            .setMediaId(song.id.toString())
            .build()
        player.addMediaItem(mediaItem)
        recordEvent(song.id, EventType.QUEUED)
    }

    override fun clearQueue() {
        player.clearMediaItems()
    }

    private fun recordEvent(songId: Long, type: EventType) {
        scope.launch(Dispatchers.IO) {
            eventDao.insertEvent(ListeningEvent(songId = songId, eventType = type))
        }
    }
}
