package com.baraka.auroramusic.dj

import android.content.Context
import android.speech.tts.TextToSpeech
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.Song
import com.baraka.auroramusic.dj.interfaces.*
import com.baraka.auroramusic.dj.models.DJCommand
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DJCommandParserImpl @Inject constructor() : DJCommandParser {
    override suspend fun parse(input: String): DJCommand {
        val text = input.lowercase()
        
        return when {
            text.contains(Regex("pause|stop music")) -> DJCommand.PauseMusic
            text.contains(Regex("resume|continue|play music")) && text.length < 15 -> DJCommand.ResumeMusic
            text.contains(Regex("skip|next")) -> DJCommand.SkipTrack
            text.contains(Regex("previous|back")) -> DJCommand.PreviousTrack
            text.contains(Regex("shuffle")) -> DJCommand.ShuffleQueue
            text.contains(Regex("clear|empty queue")) -> DJCommand.ClearQueue
            
            text.contains("dj mode") -> if (text.contains("start")) DJCommand.StartDJMode else DJCommand.StopDJMode
            
            text.contains(Regex("chill|relaxing|calm")) -> DJCommand.PlayMusic(mood = "chill")
            text.contains(Regex("energetic|high energy|workout")) -> DJCommand.PlayMusic(mood = "energetic")
            
            text.contains("more energetic") || text.contains("increase energy") -> DJCommand.IncreaseEnergy()
            text.contains("slower") || text.contains("calmer") || text.contains("decrease energy") -> DJCommand.DecreaseEnergy()
            
            text.contains("afrobeats") -> DJCommand.PlayMusic(genre = "afrobeats")
            text.contains("rock") -> DJCommand.PlayMusic(genre = "rock")
            text.contains("jazz") -> DJCommand.PlayMusic(genre = "jazz")
            
            text.contains("surprise me") -> DJCommand.PlayMusic(random = true)
            text.contains("discovery") -> DJCommand.PlayMusic(discovery = true)
            text.contains("favorite") -> DJCommand.PlayMusic(mostPlayed = true)
            text.contains("new") || text.contains("recently added") -> DJCommand.PlayMusic(recentlyAdded = true)
            
            text.contains("similar to this") -> DJCommand.PlaySimilar()
            
            // Complex pattern for "Play [Artist]"
            text.contains(Regex("play (.+)")) -> {
                val artist = Regex("play (.+)").find(text)?.groupValues?.get(1)
                DJCommand.PlayMusic(artist = artist)
            }
            
            else -> DJCommand.AskInformation(input)
        }
    }
}

@Singleton
class RecommendationEngineImpl @Inject constructor(
    private val songDao: SongDao,
    private val historyAnalyzer: ListeningHistoryAnalyzer
) : RecommendationEngine {
    override suspend fun getRecommendations(currentSong: Song?, limit: Int, discoveryLevel: Float): List<Song> {
        val allSongs = songDao.getRandomSongs(100)
        val topArtists = historyAnalyzer.getTopArtists()
        val skippedArtists = historyAnalyzer.getSkippedArtists()
        val recentIds = historyAnalyzer.getRecentlyPlayedSongIds()

        return allSongs.map { song ->
            var score = 0f
            
            // Artist match
            if (song.artist == currentSong?.artist) score += 0.5f
            if (topArtists.contains(song.artist)) score += 0.3f
            if (skippedArtists.contains(song.artist)) score -= 0.8f
            
            // Genre match
            if (song.genre == currentSong?.genre) score += 0.3f
            
            // Discovery vs Familiarity
            val isRecent = recentIds.contains(song.id)
            if (isRecent) {
                score -= (1.0f - discoveryLevel) * 0.5f // Penalty for recency
            } else {
                score += discoveryLevel * 0.4f // Bonus for novelty
            }
            
            song to score
        }.sortedByDescending { it.second }
        .take(limit)
        .map { it.first }
    }
}

@Singleton
class DJResponseGeneratorImpl @Inject constructor() : DJResponseGenerator {
    override suspend fun generateResponse(command: DJCommand): String {
        return when (command) {
            is DJCommand.PlayMusic -> "Sure, putting on some ${command.genre ?: command.mood ?: "music"}."
            is DJCommand.PauseMusic -> "Music paused."
            is DJCommand.ResumeMusic -> "Let's keep the music going."
            is DJCommand.SkipTrack -> "Skipping to the next track."
            else -> "I'm on it."
        }
    }
}

@Singleton
class DJVoiceControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DJVoiceController, TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady = true
        }
    }

    override fun speak(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun stop() {
        tts?.stop()
    }

    override val isSpeaking: Flow<Boolean> = flowOf(false) // Simplified
}

@Singleton
class PlaylistGeneratorImpl @Inject constructor(
    private val intelligenceEngine: MusicIntelligenceEngine
) : PlaylistGenerator {
    override suspend fun generate(command: DJCommand.CreatePlaylist): List<Song> {
        val playCommand = DJCommand.PlayMusic(genre = command.genre)
        return intelligenceEngine.getSongsForCommand(playCommand).take(command.count ?: 20)
    }
}

@Singleton
class DJConversationManagerImpl @Inject constructor() : DJConversationManager {
    private val history = mutableListOf<String>()
    override fun getConversationHistory(): List<String> = history
    override fun addMessage(message: String, isUser: Boolean) {
        history.add("${if (isUser) "User" else "DJ"}: $message")
    }
}
