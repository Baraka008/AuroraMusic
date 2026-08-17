package com.baraka.auroramusic.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val LAST_SONG_ID = longPreferencesKey("last_song_id")
        val LAST_POSITION = longPreferencesKey("last_position")
        val SHUFFLE_ENABLED = booleanPreferencesKey("shuffle_enabled")
        val REPEAT_MODE = intPreferencesKey("repeat_mode")
        val AMOLED_MODE = booleanPreferencesKey("amoled_mode")
    }

    val lastSongId: Flow<Long?> = context.dataStore.data.map { it[Keys.LAST_SONG_ID] }
    val lastPosition: Flow<Long> = context.dataStore.data.map { it[Keys.LAST_POSITION] ?: 0L }
    val shuffleEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHUFFLE_ENABLED] ?: false }
    val repeatMode: Flow<Int> = context.dataStore.data.map { it[Keys.REPEAT_MODE] ?: 0 }
    val amoledMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.AMOLED_MODE] ?: true }

    suspend fun savePlaybackState(songId: Long, position: Long) {
        context.dataStore.edit {
            it[Keys.LAST_SONG_ID] = songId
            it[Keys.LAST_POSITION] = position
        }
    }

    suspend fun setShuffleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SHUFFLE_ENABLED] = enabled }
    }

    suspend fun setRepeatMode(mode: Int) {
        context.dataStore.edit { it[Keys.REPEAT_MODE] = mode }
    }

    suspend fun setAmoledMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AMOLED_MODE] = enabled }
    }
}
