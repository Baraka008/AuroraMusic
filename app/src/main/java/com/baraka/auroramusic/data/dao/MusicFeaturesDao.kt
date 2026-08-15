package com.baraka.auroramusic.data.dao

import androidx.room.*
import com.baraka.auroramusic.data.entities.MusicFeatures

@Dao
interface MusicFeaturesDao {
    @Query("SELECT * FROM music_features WHERE songId = :songId")
    suspend fun getFeaturesForSong(songId: Long): MusicFeatures?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatures(features: MusicFeatures)
}
