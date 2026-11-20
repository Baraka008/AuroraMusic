# Repository audit — first pass

## Findings
- Native Android/Gradle project; Vercel is not an application host for the Android app.
- Use GitHub Actions for Gradle validation and Android APK/AAB artifacts; publish releases through GitHub Releases or Google Play when ready.
- Validate Gradle wrapper, Android SDK/NDK versions, native C++ build, permissions, background playback, and audio focus on real devices.
- README describes advanced audio features; verify each is implemented and tested before advertising it as production-ready.

## Verification
README and root Gradle structure inspected. Android build not run.