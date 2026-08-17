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

    fun setEQBand(bandIdx: Int, gainDb: Float) {
        nativeSetEQBand(bandIdx, gainDb)
    }

    fun setEQPreset(presetIdx: Int) {
        nativeSetEQPreset(presetIdx)
    }

    fun setBassBoost(gainDb: Float) {
        nativeSetBassBoost(gainDb)
    }

    fun setReverbLevel(level: Float) {
        nativeSetReverbLevel(level)
    }

    fun setVirtualizerLevel(level: Float) {
        nativeSetVirtualizerLevel(level)
    }

    fun getFFTData(): FloatArray? {
        return nativeGetFFTData()
    }

    private external fun nativeInitialize()
    private external fun nativeShutdown()
    private external fun nativeAnalyzeFeature(uri: String): FloatArray?
    private external fun nativeSetEQBand(bandIdx: Int, gainDb: Float)
    private external fun nativeSetEQPreset(presetIdx: Int)
    private external fun nativeSetBassBoost(gainDb: Float)
    private external fun nativeSetReverbLevel(level: Float)
    private external fun nativeSetVirtualizerLevel(level: Float)
    private external fun nativeGetFFTData(): FloatArray?
}
