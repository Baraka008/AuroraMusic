package com.baraka.auroramusic.audio.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.baraka.auroramusic.audio.controller.PlaybackController
import com.baraka.auroramusic.audio.controller.PlaybackControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindPlaybackController(impl: PlaybackControllerImpl): PlaybackController

    companion object {
        @Provides
        @Singleton
        fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
            return ExoPlayer.Builder(context).build()
        }
    }
}
