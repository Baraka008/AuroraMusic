# Tasks - Aurora Music Foundation

- `[/]` Configure Technology Stack (Kotlin + Compose + NDK + CMake)
    - `[ ]` Update `libs.versions.toml` with Hilt, Room, Media3, Coil, Navigation, and KSP
    - `[ ]` Update root `build.gradle.kts` with KSP and Hilt plugins
    - `[ ]` Update `app/build.gradle.kts` with NDK configuration and new dependencies
- `[ ]` Create Native Audio Foundation
    - `[ ]` Create `app/src/main/cpp` directory structure
    - `[ ]` Create `CMakeLists.txt`
    - `[ ]` Create `NativeAudioBridge.cpp` (JNI)
    - `[ ]` Create `AudioEngine` skeleton (Oboe)
- `[ ]` Create Kotlin Application Shell
    - `[ ]` Create `AuroraApplication` (Hilt)
    - `[ ]` Create `NativeAudioEngine` Kotlin wrapper
    - `[ ]` Update `MainActivity` with Hilt and basic Navigation
- `[ ]` Verification
    - `[ ]` Run `gradlew assembleDebug`
    - `[ ]` Verify JNI connection in Logcat
