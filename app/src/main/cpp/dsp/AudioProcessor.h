#ifndef AURORA_AUDIO_PROCESSOR_H
#define AURORA_AUDIO_PROCESSOR_H

#include <cstdint>

class AudioProcessor {
public:
    virtual ~AudioProcessor() = default;

    // Process a buffer of stereo interleaved floats
    virtual void process(float* buffer, int32_t numFrames) = 0;

    // Reset internal state (e.g. filter history)
    virtual void reset() = 0;

    // Enable/Disable this processor
    void setEnabled(bool enabled) { mEnabled = enabled; }
    bool isEnabled() const { return mEnabled; }

protected:
    bool mEnabled = true;
};

#endif // AURORA_AUDIO_PROCESSOR_H
