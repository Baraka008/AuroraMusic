#include "Limiter.h"
#include <cmath>

Limiter::Limiter(float thresholdDb) {
    mThreshold = pow(10.0f, thresholdDb / 20.0f);
}

void Limiter::process(float* buffer, int32_t numFrames) {
    for (int i = 0; i < numFrames * 2; ++i) {
        float sample = buffer[i];
        if (std::abs(sample) > mThreshold) {
            buffer[i] = (sample > 0) ? mThreshold : -mThreshold;
        }
    }
}

void Limiter::reset() {
    // No state to reset
}
