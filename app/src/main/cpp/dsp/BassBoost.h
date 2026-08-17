#ifndef AURORA_BASS_BOOST_H
#define AURORA_BASS_BOOST_H

#include "AudioProcessor.h"
#include "Biquad.h"

class BassBoost : public AudioProcessor {
public:
    BassBoost(float sampleRate) : mSampleRate(sampleRate) {
        setAmount(0.0f);
    }

    void process(float* buffer, int32_t numFrames) override {
        if (!mEnabled || mGainDb <= 0.0f) return;

        for (int i = 0; i < numFrames * 2; i += 2) {
            buffer[i] = mLeftFilter.process(buffer[i]);
            buffer[i+1] = mRightFilter.process(buffer[i+1]);
        }
    }

    void reset() override {
        mLeftFilter.reset();
        mRightFilter.reset();
    }

    void setAmount(float gainDb) {
        mGainDb = gainDb;
        mLeftFilter.setParams(FilterType::LowShelf, 100.0f, mSampleRate, 0.707f, gainDb);
        mRightFilter.setParams(FilterType::LowShelf, 100.0f, mSampleRate, 0.707f, gainDb);
    }

private:
    float mSampleRate;
    float mGainDb = 0.0f;
    Biquad mLeftFilter;
    Biquad mRightFilter;
};

#endif // AURORA_BASS_BOOST_H
