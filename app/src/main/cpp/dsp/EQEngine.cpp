#include "EQEngine.h"

EQEngine::EQEngine(float sampleRate) : mSampleRate(sampleRate) {
    mLeftBands.resize(10);
    mRightBands.resize(10);
    mGains.assign(10, 0.0f);

    for (int i = 0; i < 10; ++i) {
        setBandGain(i, 0.0f);
    }
}

void EQEngine::process(float* buffer, int32_t numFrames) {
    for (int i = 0; i < numFrames * 2; i += 2) {
        float left = buffer[i];
        float right = buffer[i+1];

        for (int b = 0; b < 10; ++b) {
            left = mLeftBands[b].process(left);
            right = mRightBands[b].process(right);
        }

        buffer[i] = left;
        buffer[i+1] = right;
    }
}

void EQEngine::reset() {
    for (int i = 0; i < 10; ++i) {
        mLeftBands[i].reset();
        mRightBands[i].reset();
    }
}

void EQEngine::setBandGain(int bandIdx, float gainDb) {
    if (bandIdx < 0 || bandIdx >= 10) return;

    mGains[bandIdx] = gainDb;
    float freq = mFrequencies[bandIdx];
    float Q = 1.414f; // Standard Q

    FilterType type = FilterType::Peak;
    if (bandIdx == 0) type = FilterType::LowShelf;
    if (bandIdx == 9) type = FilterType::HighShelf;

    mLeftBands[bandIdx].setParams(type, freq, mSampleRate, Q, gainDb);
    mRightBands[bandIdx].setParams(type, freq, mSampleRate, Q, gainDb);
}

float EQEngine::getBandGain(int bandIdx) const {
    if (bandIdx < 0 || bandIdx >= 10) return 0.0f;
    return mGains[bandIdx];
}

void EQEngine::setPreset(Preset preset) {
    static const float presets[][10] = {
        {0,0,0,0,0,0,0,0,0,0}, // Flat
        {4,3,2,0,-1,-1,1,2,3,4}, // Rock
        {-1,0,1,3,4,3,1,0,-1,-1}, // Pop
        {3,2,1,2,-1,-1,0,1,2,3}, // Jazz
        {4,3,1,0,0,0,1,2,3,4}, // Classic
        {5,4,2,0,1,2,3,4,5,5}, // Dance
        {5,4,1,0,-1,-1,1,1,3,4}  // HipHop
    };

    const float* gains = presets[static_cast<int>(preset)];
    for (int i = 0; i < 10; ++i) {
        setBandGain(i, gains[i]);
    }
}
