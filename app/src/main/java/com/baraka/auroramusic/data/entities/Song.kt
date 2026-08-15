package com.baraka.auroramusic.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val year: Int?,
    val duration: Long,
    val uri: String,
    val playCount: Int = 0,
    val skipCount: Int = 0,
    val isFavorite: Boolean = false,
    val lastPlayed: Long? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val rating: Int = 0,
    val userTags: String = "",
    val djExcluded: Boolean = false
)
