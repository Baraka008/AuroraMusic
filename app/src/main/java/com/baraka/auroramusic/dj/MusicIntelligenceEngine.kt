package com.baraka.auroramusic.dj

import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.Song
import com.baraka.auroramusic.dj.models.DJCommand
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicIntelligenceEngine @Inject constructor(
    private val songDao: SongDao
) {
    suspend fun getSongsForCommand(command: DJCommand.PlayMusic): List<Song> {
        return when {
            command.artist != null -> songDao.getSongsByArtist(command.artist)
            command.genre != null -> songDao.getSongsByGenre(command.genre)
            command.year != null -> songDao.getSongsByYear(command.year)
            command.recentlyAdded -> songDao.getRecentlyAdded(20)
            command.mostPlayed -> songDao.getMostPlayed(20)
            command.unheard -> songDao.getUnheardSongs()
            command.energy != null -> {
                val min = (command.energy - 0.2f).coerceAtLeast(0.0f)
                val max = (command.energy + 0.2f).coerceAtMost(1.0f)
                songDao.getSongsByEnergyRange(min, max)
            }
            command.random -> songDao.getRandomSongs(20)
            else -> songDao.getRandomSongs(10)
        }
    }
}
