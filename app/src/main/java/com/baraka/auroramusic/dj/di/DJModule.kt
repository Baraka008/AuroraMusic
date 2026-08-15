package com.baraka.auroramusic.dj.di

import com.baraka.auroramusic.dj.DJCommandParserImpl
import com.baraka.auroramusic.dj.DJContextManagerImpl
import com.baraka.auroramusic.dj.DJConversationManagerImpl
import com.baraka.auroramusic.dj.DJResponseGeneratorImpl
import com.baraka.auroramusic.dj.DJVoiceControllerImpl
import com.baraka.auroramusic.dj.PlaylistGeneratorImpl
import com.baraka.auroramusic.dj.RecommendationEngineImpl
import com.baraka.auroramusic.dj.interfaces.DJCommandParser
import com.baraka.auroramusic.dj.interfaces.DJContextManager
import com.baraka.auroramusic.dj.interfaces.DJConversationManager
import com.baraka.auroramusic.dj.interfaces.DJResponseGenerator
import com.baraka.auroramusic.dj.interfaces.DJVoiceController
import com.baraka.auroramusic.dj.interfaces.PlaylistGenerator
import com.baraka.auroramusic.dj.interfaces.RecommendationEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DJModule {

    @Binds
    @Singleton
    abstract fun bindCommandParser(impl: DJCommandParserImpl): DJCommandParser

    @Binds
    @Singleton
    abstract fun bindRecommendationEngine(impl: RecommendationEngineImpl): RecommendationEngine

    @Binds
    @Singleton
    abstract fun bindPlaylistGenerator(impl: PlaylistGeneratorImpl): PlaylistGenerator

    @Binds
    @Singleton
    abstract fun bindConversationManager(impl: DJConversationManagerImpl): DJConversationManager

    @Binds
    @Singleton
    abstract fun bindContextManager(impl: DJContextManagerImpl): DJContextManager

    @Binds
    @Singleton
    abstract fun bindResponseGenerator(impl: DJResponseGeneratorImpl): DJResponseGenerator

    @Binds
    @Singleton
    abstract fun bindVoiceController(impl: DJVoiceControllerImpl): DJVoiceController
}
