package com.baraka.auroramusic.data.dao

import androidx.room.*
import com.baraka.auroramusic.data.entities.DJSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface DJSettingsDao {
    @Query("SELECT * FROM dj_settings WHERE id = 0")
    fun getSettings(): Flow<DJSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: DJSettings)
}
