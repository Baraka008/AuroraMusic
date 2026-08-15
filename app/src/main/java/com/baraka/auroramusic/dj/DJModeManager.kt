package com.baraka.auroramusic.dj

import com.baraka.auroramusic.audio.controller.PlaybackController
import com.baraka.auroramusic.dj.interfaces.RecommendationEngine
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DJModeManager @Inject constructor(
    private val recommendationEngine: RecommendationEngine,
    private val playbackController: PlaybackController
) {
    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun startDJMode() {
        if (job?.isActive == true) return
        
        job = scope.launch {
            while (isActive) {
                // In a real app, we'd check current queue size from media controller
                // For now, we'll simulate a check every 30 seconds
                delay(30000)
                
                val recommendations = recommendationEngine.getRecommendations(
                    currentSong = null, // Should fetch from player
                    limit = 3,
                    discoveryLevel = 0.3f
                )
                
                recommendations.forEach {
                    playbackController.addToQueue(it)
                }
            }
        }
    }

    fun stopDJMode() {
        job?.cancel()
        job = null
    }
}
