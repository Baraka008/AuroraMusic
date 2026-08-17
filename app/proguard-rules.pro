# Aurora Music Proguard Rules

# Hilt/Dagger
-keep class dagger.hilt.** { *; }
-keep class com.baraka.auroramusic.di.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep class com.baraka.auroramusic.data.dao.** { *; }
-keep class com.baraka.auroramusic.data.entities.** { *; }

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }

# JNI Bridge - CRITICAL
-keepclasseswithmembernames class com.baraka.auroramusic.audio.NativeAudioEngine {
    native <methods>;
}

# Retrofit / Gson
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.baraka.auroramusic.data.api.** { *; }

# Oboe
-keep class com.google.oboe.** { *; }
