package com.baraka.auroramusic.data.api

import retrofit2.http.GET
import retrofit2.http.Query

data class LyricsResponse(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val duration: Float,
    val instrumental: Boolean,
    val plainLyrics: String?,
    val syncedLyrics: String?
)

interface LrclibApi {
    @GET("get")
    suspend fun getLyrics(
        @Query("artist_name") artistName: String,
        @Query("track_name") trackName: String,
        @Query("album_name") albumName: String?,
        @Query("duration") duration: Int // Seconds
    ): LyricsResponse
}
