#ifndef AURORA_EQ_ENGINE_H
#define AURORA_EQ_ENGINE_H

#include "AudioProcessor.h"
#include "Biquad.h"
#include <vector>

class EQEngine : public AudioProcessor {
public:
    EQEngine(float sampleRate);

    void process(float* buffer, int32_t numFrames) override;
    void reset() override;

    void setBandGain(int bandIdx, float gainDb);
    float getBandGain(int bandIdx) const;

    enum class Preset {
        Flat, Rock, Pop, Jazz, Classic, Dance, HipHop
    };
    void setPreset(Preset preset);

private:
    float mSampleRate;
    std::vector<Biquad> mLeftBands;
    std::vector<Biquad> mRightBands;
    std::vector<float> mGains;
    std::vector<float> mFrequencies = {31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
};

#endif // AURORA_EQ_ENGINE_H
