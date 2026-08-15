package com.baraka.auroramusic.dj.interfaces

import com.baraka.auroramusic.data.entities.Song
import com.baraka.auroramusic.dj.models.DJCommand
import kotlinx.coroutines.flow.Flow

interface DJCommandParser {
    suspend fun parse(input: String): DJCommand
}

interface DJConversationManager {
    fun getConversationHistory(): List<String>
    fun addMessage(message: String, isUser: Boolean)
}

interface RecommendationEngine {
    suspend fun getRecommendations(
        currentSong: Song?,
        limit: Int,
        discoveryLevel: Float
    ): List<Song>
}

interface PlaylistGenerator {
    suspend fun generate(command: DJCommand.CreatePlaylist): List<Song>
}

interface DJVoiceController {
    fun speak(text: String)
    fun stop()
    val isSpeaking: Flow<Boolean>
}

interface DJResponseGenerator {
    suspend fun generateResponse(command: DJCommand): String
}

interface AIProvider {
    suspend fun processRequest(prompt: String): String
}
