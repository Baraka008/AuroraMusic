#ifndef AURORA_SPECTRAL_ANALYZER_H
#define AURORA_SPECTRAL_ANALYZER_H

#include <vector>
#include <cstdint>
#include <mutex>

class SpectralAnalyzer {
public:
    SpectralAnalyzer(int fftSize = 1024);

    void process(float* buffer, int32_t numFrames);
    std::vector<float> getMagnitudes();

private:
    int mFftSize;
    std::vector<float> mMagnitudes;
    std::mutex mMutex;
};

#endif // AURORA_SPECTRAL_ANALYZER_H
