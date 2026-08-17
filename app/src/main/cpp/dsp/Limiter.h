#ifndef AURORA_LIMITER_H
#define AURORA_LIMITER_H

#include "AudioProcessor.h"
#include <algorithm>

class Limiter : public AudioProcessor {
public:
    Limiter(float thresholdDb = -0.1f);

    void process(float* buffer, int32_t numFrames) override;
    void reset() override;

private:
    float mThreshold;
};

#endif // AURORA_LIMITER_H
