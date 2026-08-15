#ifndef AURORA_AUDIO_ENGINE_H
#define AURORA_AUDIO_ENGINE_H

#include <oboe/Oboe.h>

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

private:
    std::shared_ptr<oboe::AudioStream> mStream;
};

#endif // AURORA_AUDIO_ENGINE_H
