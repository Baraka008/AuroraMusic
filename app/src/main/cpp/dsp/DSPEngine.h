#ifndef AURORA_DSP_ENGINE_H
#define AURORA_DSP_ENGINE_H

#include <vector>
#include <memory>
#include "AudioProcessor.h"

class DSPEngine {
public:
    DSPEngine();
    ~DSPEngine() = default;

    void process(float* buffer, int32_t numFrames);
    void reset();

    void addProcessor(std::shared_ptr<AudioProcessor> processor);

private:
    std::vector<std::shared_ptr<AudioProcessor>> mProcessors;
};

#endif // AURORA_DSP_ENGINE_H
