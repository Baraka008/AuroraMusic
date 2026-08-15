package com.baraka.auroramusic.dj

import com.baraka.auroramusic.data.dao.ListeningEventDao
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.EventType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListeningHistoryAnalyzer @Inject constructor(
    private val eventDao: ListeningEventDao,
    private val songDao: SongDao
) {
    suspend fun getTopArtists(limit: Int = 5): List<String> {
        val events = eventDao.getRecentEvents(500)
        return events.filter { it.eventType == EventType.PLAY_COMPLETED }
            .mapNotNull { event -> songDao.getSongById(event.songId)?.artist }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    suspend fun getSkippedArtists(): List<String> {
        val events = eventDao.getRecentEvents(200)
        return events.filter { it.eventType == EventType.SKIPPED }
            .mapNotNull { event -> songDao.getSongById(event.songId)?.artist }
            .distinct()
    }

    suspend fun getRecentlyPlayedSongIds(limit: Int = 50): List<Long> {
        return eventDao.getRecentEvents(limit).map { it.songId }.distinct()
    }
}
