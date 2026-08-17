#ifndef AURORA_SILENCE_SKIPPER_H
#define AURORA_SILENCE_SKIPPER_H

#include "AudioProcessor.h"
#include <cmath>

class SilenceSkipper : public AudioProcessor {
public:
    SilenceSkipper(float thresholdDb = -60.0f)
        : mThreshold(pow(10.0f, thresholdDb / 20.0f)) {}

    void process(float* buffer, int32_t numFrames) override {
        if (!mEnabled) return;

        // Simple implementation: check if current buffer is silent.
        // In a real skipper, we'd need to tell the engine to skip ahead.
        // For this DSP node, we just detect it.
        float maxVal = 0.0f;
        for (int i = 0; i < numFrames * 2; ++i) {
            maxVal = std::max(maxVal, std::abs(buffer[i]));
        }
        mIsSilent = (maxVal < mThreshold);
    }

    void reset() override { mIsSilent = false; }

    bool isSilent() const { return mIsSilent; }

private:
    float mThreshold;
    bool mIsSilent = false;
};

#endif // AURORA_SILENCE_SKIPPER_H
