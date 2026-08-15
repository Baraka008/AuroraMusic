package com.baraka.auroramusic.audio

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAudioEngine @Inject constructor() {

    init {
        System.loadLibrary("aurora-audio")
    }

    fun initialize() {
        nativeInitialize()
    }

    fun shutdown() {
        nativeShutdown()
    }

    fun analyzeFeature(uri: String): FloatArray? {
        return nativeAnalyzeFeature(uri)
    }

    private external fun nativeInitialize()
    private external fun nativeShutdown()
    private external fun nativeAnalyzeFeature(uri: String): FloatArray?
}
