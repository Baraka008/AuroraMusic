cmake_minimum_required(VERSION 3.22.1)

project("aurora-audio")

# Find Oboe via Prefab
find_package(oboe REQUIRED CONFIG)

# Define the native library
add_library(aurora-audio SHARED
    jni/NativeAudioBridge.cpp
    engine/AudioEngine.cpp
    dsp/DSPEngine.cpp
    dsp/EQEngine.cpp
    dsp/Limiter.cpp
    analysis/SpectralAnalyzer.cpp
)

# Include directories
target_include_directories(aurora-audio PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/engine
    ${CMAKE_CURRENT_SOURCE_DIR}/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/dsp
    ${CMAKE_CURRENT_SOURCE_DIR}/analysis
)

# Link libraries
target_link_libraries(aurora-audio
    oboe::oboe
    log
    android
)
