package com.baraka.auroramusic.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "music_features",
    foreignKeys = [
        ForeignKey(
            entity = Song::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MusicFeatures(
    @PrimaryKey val songId: Long,
    val energy: Float,
    val tempo: Float,
    val danceability: Float,
    val acousticness: Float,
    val intensity: Float,
    val brightness: Float,
    val calmness: Float,
    val instrumentalness: Float
)
