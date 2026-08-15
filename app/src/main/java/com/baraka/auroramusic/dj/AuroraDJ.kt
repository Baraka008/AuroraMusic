package com.baraka.auroramusic.dj

import com.baraka.auroramusic.audio.controller.PlaybackController
import com.baraka.auroramusic.dj.interfaces.*
import com.baraka.auroramusic.dj.models.DJCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuroraDJ @Inject constructor(
    private val commandParser: DJCommandParser,
    private val intelligenceEngine: MusicIntelligenceEngine,
    private val playbackController: PlaybackController,
    private val contextManager: DJContextManager,
    private val responseGenerator: DJResponseGenerator,
    private val voiceController: DJVoiceController,
    private val djModeManager: DJModeManager,
    private val playlistGenerator: PlaylistGenerator
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun processInput(input: String) {
        scope.launch {
            val baseCommand = commandParser.parse(input)
            val finalCommand = contextManager.getMergedCommand(baseCommand)
            
            executeCommand(finalCommand)
            
            val response = responseGenerator.generateResponse(finalCommand)
            voiceController.speak(response)
            
            contextManager.updateContext(finalCommand)
        }
    }

    private suspend fun executeCommand(command: DJCommand) {
        when (command) {
            is DJCommand.PlayMusic -> {
                val songs = intelligenceEngine.getSongsForCommand(command)
                if (songs.isNotEmpty()) {
                    if (command.replaceQueue) {
                        playbackController.setQueue(songs)
                    } else {
                        playbackController.addToQueue(songs.first())
                    }
                    playbackController.play(songs.first())
                }
            }
            is DJCommand.PauseMusic -> playbackController.pause()
            is DJCommand.ResumeMusic -> playbackController.resume()
            is DJCommand.SkipTrack -> playbackController.skip()
            is DJCommand.PreviousTrack -> playbackController.previous()
            is DJCommand.ShuffleQueue -> { /* Implement shuffle logic */ }
            is DJCommand.ClearQueue -> playbackController.clearQueue()
            is DJCommand.StartDJMode -> djModeManager.startDJMode()
            is DJCommand.StopDJMode -> djModeManager.stopDJMode()
            is DJCommand.CreatePlaylist -> {
                val songs = playlistGenerator.generate(command)
                playbackController.setQueue(songs)
                playbackController.play(songs.first())
            }
            else -> { /* Handle other commands */ }
        }
    }
}
