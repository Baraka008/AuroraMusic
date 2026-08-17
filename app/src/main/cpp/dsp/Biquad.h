#ifndef AURORA_BIQUAD_H
#define AURORA_BIQUAD_H

#include <cmath>

enum class FilterType {
    Peak,
    LowShelf,
    HighShelf
};

class Biquad {
public:
    Biquad() = default;

    void setParams(FilterType type, float frequency, float sampleRate, float Q, float gainDb) {
        float A = pow(10.0f, gainDb / 40.0f);
        float omega = 2.0f * M_PI * frequency / sampleRate;
        float sn = sin(omega);
        float cs = cos(omega);
        float alpha = sn / (2.0f * Q);

        switch (type) {
            case FilterType::Peak:
                b0 = 1.0f + alpha * A;
                b1 = -2.0f * cs;
                b2 = 1.0f - alpha * A;
                a0 = 1.0f + alpha / A;
                a1 = -2.0f * cs;
                a2 = 1.0f - alpha / A;
                break;
            case FilterType::LowShelf:
                b0 = A * ((A + 1.0f) - (A - 1.0f) * cs + 2.0f * sqrt(A) * alpha);
                b1 = 2.0f * A * ((A - 1.0f) - (A + 1.0f) * cs);
                b2 = A * ((A + 1.0f) - (A - 1.0f) * cs - 2.0f * sqrt(A) * alpha);
                a0 = (A + 1.0f) + (A - 1.0f) * cs + 2.0f * sqrt(A) * alpha;
                a1 = -2.0f * ((A - 1.0f) + (A + 1.0f) * cs);
                a2 = (A + 1.0f) + (A - 1.0f) * cs - 2.0f * sqrt(A) * alpha;
                break;
            case FilterType::HighShelf:
                b0 = A * ((A + 1.0f) + (A - 1.0f) * cs + 2.0f * sqrt(A) * alpha);
                b1 = -2.0f * A * ((A - 1.0f) + (A + 1.0f) * cs);
                b2 = A * ((A + 1.0f) + (A - 1.0f) * cs - 2.0f * sqrt(A) * alpha);
                a0 = (A + 1.0f) - (A - 1.0f) * cs + 2.0f * sqrt(A) * alpha;
                a1 = 2.0f * ((A - 1.0f) - (A + 1.0f) * cs);
                a2 = (A + 1.0f) - (A - 1.0f) * cs - 2.0f * sqrt(A) * alpha;
                break;
        }

        // Normalize
        b0 /= a0; b1 /= a0; b2 /= a0;
        a1 /= a0; a2 /= a0;
    }

    inline float process(float x) {
        float y = b0 * x + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;
        x2 = x1; x1 = x;
        y2 = y1; y1 = y;
        return y;
    }

    void reset() {
        x1 = x2 = y1 = y2 = 0.0f;
    }

private:
    float b0, b1, b2, a0, a1, a2;
    float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
};

#endif // AURORA_BIQUAD_H
