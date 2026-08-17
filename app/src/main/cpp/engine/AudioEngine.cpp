#include "AudioEngine.h"
#include "dsp/EQEngine.h"
#include "dsp/Limiter.h"
#include "dsp/SilenceSkipper.h"
#include "dsp/BassBoost.h"
#include "dsp/ReverbProcessor.h"
#include "dsp/VirtualizerProcessor.h"

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
        float sampleRate = (float)mStream->getSampleRate();

        // Add default processors
        mEq = std::make_shared<EQEngine>(sampleRate);
        mBassBoost = std::make_shared<BassBoost>(sampleRate);
        mReverb = std::make_shared<ReverbProcessor>(sampleRate);
        mVirtualizer = std::make_shared<VirtualizerProcessor>(sampleRate);

        mDsp.addProcessor(mEq);
        mDsp.addProcessor(mBassBoost);
        mDsp.addProcessor(mReverb);
        mDsp.addProcessor(mVirtualizer);
        mDsp.addProcessor(std::make_shared<SilenceSkipper>());
        mDsp.addProcessor(std::make_shared<Limiter>());

        mStream->requestStart();
    }
}

void AudioEngine::setEQBand(int bandIdx, float gainDb) {
    if (mEq) mEq->setBandGain(bandIdx, gainDb);
}

void AudioEngine::setEQPreset(int presetIdx) {
    if (mEq) mEq->setPreset(static_cast<EQEngine::Preset>(presetIdx));
}

void AudioEngine::setBassBoost(float gainDb) {
    if (mBassBoost) mBassBoost->setAmount(gainDb);
}

void AudioEngine::setReverbLevel(float level) {
    if (mReverb) mReverb->setLevel(level);
}

void AudioEngine::setVirtualizerLevel(float level) {
    if (mVirtualizer) mVirtualizer->setLevel(level);
}

AudioEngine::~AudioEngine() {
    if (mStream) {
        mStream->stop();
        mStream->close();
    }
}

oboe::DataCallbackResult AudioEngine::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {
    float *output = static_cast<float *>(audioData);

    // In a real app, we'd read from a decoder here.
    // For now, let's assume 'output' has audio data or keep it silence for testing signal path.
    // (Actual playback is handled by ExoPlayer in this app, so this Oboe engine
    // would normally be used for low-latency effects or a custom mixer).

    // Process through DSP
    mDsp.process(output, numFrames);

    // Analyze for visualization
    mAnalyzer.process(output, numFrames);

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
