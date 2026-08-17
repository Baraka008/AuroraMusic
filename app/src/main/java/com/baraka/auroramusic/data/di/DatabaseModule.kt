package com.baraka.auroramusic.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.baraka.auroramusic.data.AuroraDatabase
import com.baraka.auroramusic.data.dao.DJSettingsDao
import com.baraka.auroramusic.data.dao.ListeningEventDao
import com.baraka.auroramusic.data.dao.MusicFeaturesDao
import com.baraka.auroramusic.data.dao.PlaylistDao
import com.baraka.auroramusic.data.dao.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AuroraDatabase {
        return Room.databaseBuilder(
            context,
            AuroraDatabase::class.java,
            "aurora_music.db"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT OR IGNORE INTO dj_settings (id, voiceEnabled, commentaryEnabled, commentaryFrequency, discoveryLevel, artistRepetitionLimit, songRepetitionLimitHours, preferredGenres, excludedGenres, preferredArtists, excludedArtists, aiProvider) VALUES (0, 1, 1, 'NORMAL', 0.3, 3, 2, '', '', '', '', 'LOCAL')")
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("INSERT OR IGNORE INTO dj_settings (id, voiceEnabled, commentaryEnabled, commentaryFrequency, discoveryLevel, artistRepetitionLimit, songRepetitionLimitHours, preferredGenres, excludedGenres, preferredArtists, excludedArtists, aiProvider) VALUES (0, 1, 1, 'NORMAL', 0.3, 3, 2, '', '', '', '', 'LOCAL')")
            }
        }).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSongDao(database: AuroraDatabase): SongDao = database.songDao()

    @Provides
    fun provideMusicFeaturesDao(database: AuroraDatabase): MusicFeaturesDao = database.musicFeaturesDao()

    @Provides
    fun provideListeningEventDao(database: AuroraDatabase): ListeningEventDao = database.listeningEventDao()

    @Provides
    fun provideDJSettingsDao(database: AuroraDatabase): DJSettingsDao = database.djSettingsDao()

    @Provides
    fun providePlaylistDao(database: AuroraDatabase): PlaylistDao = database.playlistDao()
}
