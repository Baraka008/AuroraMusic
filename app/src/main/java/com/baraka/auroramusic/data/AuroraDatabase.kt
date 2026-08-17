package com.baraka.auroramusic.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.baraka.auroramusic.data.dao.DJSettingsDao
import com.baraka.auroramusic.data.dao.ListeningEventDao
import com.baraka.auroramusic.data.dao.MusicFeaturesDao
import com.baraka.auroramusic.data.dao.PlaylistDao
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.DJSettings
import com.baraka.auroramusic.data.entities.ListeningEvent
import com.baraka.auroramusic.data.entities.MusicFeatures
import com.baraka.auroramusic.data.entities.Playlist
import com.baraka.auroramusic.data.entities.PlaylistSongCrossRef
import com.baraka.auroramusic.data.entities.Song

@Database(
    entities = [
        Song::class,
        MusicFeatures::class,
        ListeningEvent::class,
        DJSettings::class,
        Playlist::class,
        PlaylistSongCrossRef::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AuroraDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun musicFeaturesDao(): MusicFeaturesDao
    abstract fun listeningEventDao(): ListeningEventDao
    abstract fun djSettingsDao(): DJSettingsDao
    abstract fun playlistDao(): PlaylistDao
}
