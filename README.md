# Aurora Music 🎵

Aurora Music is a high-performance, professional-grade music player for Android. Built with a focus on audio quality and a modern "Midnight Aurora" aesthetic, it leverages the power of C++ for high-fidelity audio processing and Jetpack Compose for a fluid, adaptive user interface.

## ✨ Key Features

### 🎧 Elite Audio Engine
- **Native C++ (Oboe)**: Zero-latency audio processing for professional sound quality.
- **High-Fidelity Visualizer**: Real-time FFT-based visualization that dances with your music.
- **Pro Audio Effects**: 
    - 10-band Equalizer with high-quality presets.
    - Deep Bass Boost and Immersion Virtualizer.
    - Advanced Reverb (Room/Hall simulation) using Feedback Delay Networks.
- **Continuous Playback**: Seamlessly transitions between tracks with customizable crossfade.

### 🌓 "Midnight Aurora" Aesthetics
- **AMOLED Black Design**: Pure black UI optimized for OLED displays and battery savings.
- **Modern Palette**: Sophisticated, muted tones (Slate Blue, Deep Indigo) for a premium feel.
- **Adaptive Interface**: Looks perfect on phones, foldables, and tablets using Material 3 Window Size Classes.
- **Glassmorphism**: Translucent "glass" surfaces for a layered, contemporary look.

### 📚 Smart Library & Navigation
- **Multi-Faceted Browsing**: Explore by Song, Artist, Album, Genre, or directly via Folder.
- **Timed Lyrics (LRC)**: Automatic lyrics fetching from LRCLIB with auto-scrolling synchronization.
- **Persistent Memory**: Remembers your exact track and position across app restarts.
- **Full Playlist System**: Create and manage custom collections easily.

### 🚀 System Integration
- **Floating Mini-Player**: Modern, hovering control bar that stays with you as you browse.
- **Output Device Detection**: Real-time indicator of your playback device (Bluetooth, Speaker, etc.).
- **Smart Integration**: Media notification controls, headset hooks, and audio focus management.

## 🛠 Tech Stack
- **Languages**: Kotlin (UI & Logic), C++ (Audio Engine).
- **UI Framework**: Jetpack Compose (Material 3).
- **Audio Library**: Google Oboe.
- **Architecture**: MVI/MVVM with Hilt (DI).
- **Data**: Room Database, Jetpack DataStore.
- **Networking**: Retrofit & OkHttp for Lyrics fetching.

## 📥 Download & Live Demo
Visit our landing page to download the latest APK:
👉 [Aurora Music Landing Page](https://victorbaraka.github.io/AuroraMusic)

## 🏗 Build Instructions
1. Clone the repository.
2. Open in Android Studio (Koala or later).
3. Ensure NDK (Side by side) is installed.
4. Sync Gradle and run the `:app` module.

---
Created with ❤️ by Victor Baraka.
