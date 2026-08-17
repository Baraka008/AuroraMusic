package com.baraka.auroramusic.data

import com.baraka.auroramusic.data.api.LrclibApi
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepository @Inject constructor(
    private val songDao: SongDao,
    private val lrclibApi: LrclibApi
) {
    suspend fun getLyricsForSong(song: Song): String? = withContext(Dispatchers.IO) {
        if (!song.embeddedLyrics.isNullOrBlank()) {
            return@withContext song.embeddedLyrics
        }

        try {
            val response = lrclibApi.getLyrics(
                artistName = song.artist,
                trackName = song.title,
                albumName = song.album,
                duration = (song.duration / 1000).toInt()
            )

            val lyrics = response.syncedLyrics ?: response.plainLyrics
            if (!lyrics.isNullOrBlank()) {
                // Cache the lyrics in DB
                songDao.updateSong(song.copy(embeddedLyrics = lyrics))
                return@withContext lyrics
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }
}
