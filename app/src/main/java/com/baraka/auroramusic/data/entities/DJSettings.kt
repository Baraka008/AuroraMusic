package com.baraka.auroramusic.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dj_settings")
data class DJSettings(
    @PrimaryKey val id: Int = 0, // Singleton record
    val voiceEnabled: Boolean = true,
    val commentaryEnabled: Boolean = true,
    val commentaryFrequency: String = "NORMAL", // LOW, NORMAL, HIGH
    val discoveryLevel: Float = 0.3f, // 0.0 to 1.0
    val artistRepetitionLimit: Int = 3,
    val songRepetitionLimitHours: Int = 2,
    val preferredGenres: String = "",
    val excludedGenres: String = "",
    val preferredArtists: String = "",
    val excludedArtists: String = "",
    val aiProvider: String = "LOCAL" // LOCAL, REMOTE
)
