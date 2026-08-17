#include "SpectralAnalyzer.h"
#include <cmath>
#include <algorithm>
#include <complex>

SpectralAnalyzer::SpectralAnalyzer(int fftSize) : mFftSize(fftSize) {
    mMagnitudes.assign(fftSize / 2, 0.0f);
}

// Basic Radix-2 FFT implementation
void fft(std::vector<std::complex<float>>& x) {
    int n = x.size();
    if (n <= 1) return;

    std::vector<std::complex<float>> even(n / 2);
    std::vector<std::complex<float>> odd(n / 2);
    for (int i = 0; i < n / 2; ++i) {
        even[i] = x[i * 2];
        odd[i] = x[i * 2 + 1];
    }

    fft(even);
    fft(odd);

    for (int k = 0; k < n / 2; ++k) {
        std::complex<float> t = std::polar(1.0f, -2.0f * (float)M_PI * k / n) * odd[k];
        x[k] = even[k] + t;
        x[k + n / 2] = even[k] - t;
    }
}

void SpectralAnalyzer::process(float* buffer, int32_t numFrames) {
    std::lock_guard<std::mutex> lock(mMutex);

    // Prepare input for FFT
    std::vector<std::complex<float>> data(mFftSize, 0.0f);
    for (int i = 0; i < mFftSize && i < numFrames; ++i) {
        // Average stereo to mono for analysis
        data[i] = (buffer[i * 2] + buffer[i * 2 + 1]) * 0.5f;
        // Apply Hanning window
        float window = 0.5f * (1.0f - cos(2.0f * M_PI * i / (mFftSize - 1)));
        data[i] *= window;
    }

    // Since we don't have a full FFT library, let's use a faster bit-reversal implementation
    // to avoid recursion depth issues in real-time.
    int n = mFftSize;
    for (int i = 1, j = 0; i < n; i++) {
        int bit = n >> 1;
        for (; j & bit; bit >>= 1) j ^= bit;
        j ^= bit;
        if (i < j) std::swap(data[i], data[j]);
    }

    for (int len = 2; len <= n; len <<= 1) {
        float ang = 2.0f * M_PI / len * -1;
        std::complex<float> wlen(cos(ang), sin(ang));
        for (int i = 0; i < n; i += len) {
            std::complex<float> w(1);
            for (int j = 0; j < len / 2; j++) {
                std::complex<float> u = data[i + j], v = data[i + j + len / 2] * w;
                data[i + j] = u + v;
                data[i + j + len / 2] = u - v;
                w *= wlen;
            }
        }
    }

    // Calculate magnitudes
    for (int i = 0; i < mMagnitudes.size(); ++i) {
        float mag = std::abs(data[i]);
        // Simple smoothing
        mMagnitudes[i] = mMagnitudes[i] * 0.8f + mag * 0.2f;
    }
}

std::vector<float> SpectralAnalyzer::getMagnitudes() {
    std::lock_guard<std::mutex> lock(mMutex);
    return mMagnitudes;
}
