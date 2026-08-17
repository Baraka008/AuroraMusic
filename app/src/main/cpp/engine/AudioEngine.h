#ifndef AURORA_AUDIO_ENGINE_H
#define AURORA_AUDIO_ENGINE_H

#include <oboe/Oboe.h>
#include "dsp/DSPEngine.h"
#include "dsp/EQEngine.h"
#include "dsp/BassBoost.h"
#include "dsp/ReverbProcessor.h"
#include "dsp/VirtualizerProcessor.h"
#include "analysis/SpectralAnalyzer.h"

class AudioEngine : public oboe::AudioStreamDataCallback {
public:
    AudioEngine();
    ~AudioEngine();

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override;

    struct AudioFeatures {
        float energy;
        float tempo;
        float intensity;
    };

    AudioFeatures analyzeFeature(const char* uri);

    DSPEngine& getDSP() { return mDsp; }
    SpectralAnalyzer& getAnalyzer() { return mAnalyzer; }

    void setEQBand(int bandIdx, float gainDb);
    void setEQPreset(int presetIdx);
    void setBassBoost(float gainDb);
    void setReverbLevel(float level);
    void setVirtualizerLevel(float level);

private:
    std::shared_ptr<oboe::AudioStream> mStream;
    DSPEngine mDsp;
    SpectralAnalyzer mAnalyzer;
    std::shared_ptr<EQEngine> mEq;
    std::shared_ptr<BassBoost> mBassBoost;
    std::shared_ptr<ReverbProcessor> mReverb;
    std::shared_ptr<VirtualizerProcessor> mVirtualizer;
};

#endif // AURORA_AUDIO_ENGINE_H
