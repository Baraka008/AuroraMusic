#ifndef AURORA_REVERB_PROCESSOR_H
#define AURORA_REVERB_PROCESSOR_H

#include "AudioProcessor.h"
#include <vector>

class ReverbProcessor : public AudioProcessor {
public:
    ReverbProcessor(float sampleRate) : mSampleRate(sampleRate) {
        mLevel = 0.0f;
        // Initialize delay lines for a simple comb filter bank
        mDelays.resize(4);
        mDelays[0].assign(static_cast<int>(0.0297f * sampleRate), 0.0f);
        mDelays[1].assign(static_cast<int>(0.0371f * sampleRate), 0.0f);
        mDelays[2].assign(static_cast<int>(0.0411f * sampleRate), 0.0f);
        mDelays[3].assign(static_cast<int>(0.0437f * sampleRate), 0.0f);
        mWriteIndices.assign(4, 0);
    }

    void process(float* buffer, int32_t numFrames) override {
        if (!mEnabled || mLevel <= 0.0f) return;

        for (int i = 0; i < numFrames * 2; i += 2) {
            float dryL = buffer[i];
            float dryR = buffer[i+1];
            float mono = (dryL + dryR) * 0.5f;
            float wet = 0.0f;

            for (int d = 0; d < 4; ++d) {
                float delayed = mDelays[d][mWriteIndices[d]];
                wet += delayed;
                mDelays[d][mWriteIndices[d]] = mono + (delayed * 0.7f);
                mWriteIndices[d] = (mWriteIndices[d] + 1) % mDelays[d].size();
            }

            buffer[i] = dryL + (wet * 0.2f * mLevel);
            buffer[i+1] = dryR + (wet * 0.2f * mLevel);
        }
    }

    void reset() override {
        for (auto& d : mDelays) std::fill(d.begin(), d.end(), 0.0f);
    }

    void setLevel(float level) { mLevel = level; }

private:
    float mSampleRate;
    float mLevel;
    std::vector<std::vector<float>> mDelays;
    std::vector<int> mWriteIndices;
};

#endif // AURORA_REVERB_PROCESSOR_H
