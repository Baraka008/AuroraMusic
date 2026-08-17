#include <jni.h>
#include <android/log.h>
#include "AudioEngine.h"

#define LOG_TAG "AuroraAudioBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static AudioEngine *engine = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeInitialize(JNIEnv *env, jobject thiz) {
    if (engine == nullptr) {
        engine = new AudioEngine();
        LOGI("Aurora Audio Engine Initialized");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeShutdown(JNIEnv *env, jobject thiz) {
    if (engine != nullptr) {
        delete engine;
        engine = nullptr;
        LOGI("Aurora Audio Engine Shutdown");
    }
}

#include "dsp/EQEngine.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeSetEQBand(JNIEnv *env, jobject thiz, jint band_idx, jfloat gain_db) {
    if (engine != nullptr) {
        engine->setEQBand(band_idx, gain_db);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeSetEQPreset(JNIEnv *env, jobject thiz, jint preset_idx) {
    if (engine != nullptr) {
        engine->setEQPreset(preset_idx);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeSetBassBoost(JNIEnv *env, jobject thiz, jfloat gain_db) {
    if (engine != nullptr) {
        engine->setBassBoost(gain_db);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeSetReverbLevel(JNIEnv *env, jobject thiz, jfloat level) {
    if (engine != nullptr) {
        engine->setReverbLevel(level);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeSetVirtualizerLevel(JNIEnv *env, jobject thiz, jfloat level) {
    if (engine != nullptr) {
        engine->setVirtualizerLevel(level);
    }
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeGetFFTData(JNIEnv *env, jobject thiz) {
    if (engine == nullptr) return nullptr;

    auto magnitudes = engine->getAnalyzer().getMagnitudes();
    jfloatArray result = env->NewFloatArray(magnitudes.size());
    env->SetFloatArrayRegion(result, 0, magnitudes.size(), magnitudes.data());
    return result;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_baraka_auroramusic_audio_NativeAudioEngine_nativeAnalyzeFeature(JNIEnv *env, jobject thiz, jstring uri) {
    if (engine == nullptr) return nullptr;

    const char *nativeUri = env->GetStringUTFChars(uri, nullptr);
    AudioEngine::AudioFeatures features = engine->analyzeFeature(nativeUri);
    env->ReleaseStringUTFChars(uri, nativeUri);

    // Create a HashMap to return multiple features or a dedicated Kotlin data class
    // For simplicity, we'll assume a constructor for MusicFeatures exists or use a simple mapping.
    // In this case, we'll just return a float array to avoid complex JNI object creation.

    jfloatArray result = env->NewFloatArray(3);
    jfloat fill[3];
    fill[0] = features.energy;
    fill[1] = features.tempo;
    fill[2] = features.intensity;
    env->SetFloatArrayRegion(result, 0, 3, fill);

    return result;
}
