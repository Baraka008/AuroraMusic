# Implementation Plan - Aurora Music Foundation

This plan covers Phase 1 through Phase 5 of the Aurora Music project: setting up the project foundation, configuring the technology stack (Kotlin, Compose, NDK, CMake), and establishing the core architecture.

## User Review Required

> [!IMPORTANT]
> I have identified that the project currently lacks Hilt, Room, Media3, and NDK configurations. I will be adding these dependencies and setting up the C++ build pipeline.

> [!NOTE]
> The current `compileSdk` and `targetSdk` are set to 36. I will maintain this as it is the latest, but ensure compatibility with the `minSdk` of 24.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/gradle/libs.versions.toml)
- Add versions and library definitions for:
    - Hilt (DI)
    - Room (Database)
    - Media3 (ExoPlayer, MediaSession)
    - Coil (Image loading)
    - DataStore (Preferences)
    - WorkManager
    - Navigation Compose

#### [MODIFY] [build.gradle.kts (root)](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/build.gradle.kts)
- Add Hilt and KSP plugins.

#### [MODIFY] [app/build.gradle.kts](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/build.gradle.kts)
- Apply Hilt and KSP plugins.
- Configure `externalNativeBuild` for CMake.
- Add dependencies for Hilt, Room, Media3, Coil, etc.

### Native Foundation

#### [NEW] [CMakeLists.txt](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/cpp/CMakeLists.txt)
- Configure the native library `aurora-audio`.

#### [NEW] [NativeAudioBridge.cpp](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/cpp/jni/NativeAudioBridge.cpp)
- Basic JNI interface for engine initialization and playback control.

#### [NEW] [AudioEngine.h](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/cpp/engine/AudioEngine.h) / [AudioEngine.cpp](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/cpp/engine/AudioEngine.cpp)
- Skeleton for the Oboe-based audio engine.

### Kotlin Foundation

#### [NEW] [AuroraApplication.kt](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/java/com/baraka/auroramusic/AuroraApplication.kt)
- Hilt Application class.

#### [NEW] [NativeAudioEngine.kt](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/java/com/baraka/auroramusic/audio/NativeAudioEngine.kt)
- Kotlin wrapper for JNI calls.

#### [MODIFY] [MainActivity.kt](file:///home/victorbaraka/AndroidStudioProjects/AuroraMusic/app/src/main/java/com/baraka/auroramusic/MainActivity.kt)
- Annotate with `@AndroidEntryPoint`.
- Setup basic Navigation shell.

## Verification Plan

### Automated Tests
- `gradlew assembleDebug` to verify build configuration and C++ compilation.
- Basic Unit Test to verify Hilt injection.

### Manual Verification
- Deploy to an Android device/emulator.
- Check Logcat for "Aurora Audio Engine Initialized" message from C++.
