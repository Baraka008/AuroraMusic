#include "AudioEngine.h"

AudioEngine::AudioEngine() {
    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output)
           ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
           ->setSharingMode(oboe::SharingMode::Exclusive)
           ->setFormat(oboe::AudioFormat::Float)
           ->setChannelCount(oboe::ChannelCount::Stereo)
           ->setDataCallback(this);

    builder.openStream(mStream);
    if (mStream) {
        mStream->requestStart();
    }
}

AudioEngine::~AudioEngine() {
    if (mStream) {
        mStream->stop();
        mStream->close();
    }
}

oboe::DataCallbackResult AudioEngine::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {
    // Fill with silence for now
    float *output = static_cast<float *>(audioData);
    for (int i = 0; i < numFrames * 2; ++i) {
        output[i] = 0.0f;
    }
    return oboe::DataCallbackResult::Continue;
}

AudioEngine::AudioFeatures AudioEngine::analyzeFeature(const char* uri) {
    // Simulated realistic metrics based on file path hashing to be consistent per song
    size_t hash = std::hash<std::string>{}(uri);

    AudioFeatures features;
    features.energy = 0.4f + (hash % 60) / 100.0f;   // 0.4 to 1.0
    features.tempo = 70.0f + (hash % 90);           // 70 to 160 BPM
    features.intensity = 0.3f + (hash % 50) / 100.0f; // 0.3 to 0.8

    return features;
}
