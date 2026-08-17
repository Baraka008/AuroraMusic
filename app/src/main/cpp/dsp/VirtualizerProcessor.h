#ifndef AURORA_VIRTUALIZER_PROCESSOR_H
#define AURORA_VIRTUALIZER_PROCESSOR_H

#include "AudioProcessor.h"

class VirtualizerProcessor : public AudioProcessor {
public:
    VirtualizerProcessor(float sampleRate) : mSampleRate(sampleRate) {
        mLevel = 0.0f;
    }

    void process(float* buffer, int32_t numFrames) override {
        if (!mEnabled || mLevel <= 0.0f) return;

        // Simple stereo expansion/widening
        for (int i = 0; i < numFrames * 2; i += 2) {
            float l = buffer[i];
            float r = buffer[i+1];

            float mid = (l + r) * 0.5f;
            float side = (l - r) * 0.5f;

            // Boost side content for widening
            side *= (1.0f + mLevel);

            buffer[i] = mid + side;
            buffer[i+1] = mid - side;
        }
    }

    void reset() override {}

    void setLevel(float level) { mLevel = level; }

private:
    float mSampleRate;
    float mLevel;
};

#endif // AURORA_VIRTUALIZER_PROCESSOR_H
