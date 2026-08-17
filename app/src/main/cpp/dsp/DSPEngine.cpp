#include "DSPEngine.h"

DSPEngine::DSPEngine() {
    // Initial engines will be added here
}

void DSPEngine::process(float* buffer, int32_t numFrames) {
    for (auto& processor : mProcessors) {
        if (processor->isEnabled()) {
            processor->process(buffer, numFrames);
        }
    }
}

void DSPEngine::reset() {
    for (auto& processor : mProcessors) {
        processor->reset();
    }
}

void DSPEngine::addProcessor(std::shared_ptr<AudioProcessor> processor) {
    mProcessors.push_back(processor);
}
