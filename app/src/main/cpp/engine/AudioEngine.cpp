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
    // In a real implementation, we would decode the audio file at 'uri'
    // and perform RMS, FFT, and beat tracking.
    // Here we simulate the analysis for the architectural flow.

    AudioFeatures features;
    features.energy = 0.75f;   // Simulated value
    features.tempo = 124.0f;   // Simulated BPM
    features.intensity = 0.6f; // Simulated value

    return features;
}
