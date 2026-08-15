package com.baraka.auroramusic.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class EventType {
    PLAY_STARTED,
    PLAY_COMPLETED,
    SKIPPED,
    FAVORITED,
    UNFAVORITED,
    QUEUED,
    REMOVED_FROM_QUEUE
}

@Entity(
    tableName = "listening_events",
    foreignKeys = [
        ForeignKey(
            entity = Song::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ListeningEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val songId: Long,
    val eventType: EventType,
    val timestamp: Long = System.currentTimeMillis()
)
