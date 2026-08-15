package com.baraka.auroramusic.data.dao

import androidx.room.*
import com.baraka.auroramusic.data.entities.ListeningEvent

@Dao
interface ListeningEventDao {
    @Query("SELECT * FROM listening_events ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentEvents(limit: Int): List<ListeningEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ListeningEvent)

    @Query("DELETE FROM listening_events WHERE timestamp < :timestamp")
    suspend fun deleteOldEvents(timestamp: Long)
}
