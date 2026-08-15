package com.baraka.auroramusic.dj

import com.baraka.auroramusic.audio.NativeAudioEngine
import com.baraka.auroramusic.data.dao.MusicFeaturesDao
import com.baraka.auroramusic.data.entities.MusicFeatures
import com.baraka.auroramusic.data.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicFeatureAnalyzer @Inject constructor(
    private val nativeEngine: NativeAudioEngine,
    private val featuresDao: MusicFeaturesDao
) {
    suspend fun analyzeSong(song: Song) = withContext(Dispatchers.Default) {
        val rawFeatures = nativeEngine.analyzeFeature(song.uri)
        if (rawFeatures != null && rawFeatures.size >= 3) {
            val features = MusicFeatures(
                songId = song.id,
                energy = rawFeatures[0],
                tempo = rawFeatures[1],
                intensity = rawFeatures[2],
                danceability = 0.5f, // Default for now
                acousticness = 0.5f,
                brightness = 0.5f,
                calmness = 0.5f,
                instrumentalness = 0.5f
            )
            featuresDao.insertFeatures(features)
        }
    }
}
