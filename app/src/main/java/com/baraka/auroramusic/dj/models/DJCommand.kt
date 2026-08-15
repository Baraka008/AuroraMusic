package com.baraka.auroramusic.dj.models

sealed class DJCommand {
    data class PlayMusic(
        val genre: String? = null,
        val mood: String? = null,
        val energy: Float? = null,
        val artist: String? = null,
        val album: String? = null,
        val year: Int? = null,
        val discovery: Boolean = false,
        val unheard: Boolean = false,
        val recentlyAdded: Boolean = false,
        val mostPlayed: Boolean = false,
        val random: Boolean = false,
        val replaceQueue: Boolean = true
    ) : DJCommand()

    object PauseMusic : DJCommand()
    object ResumeMusic : DJCommand()
    object SkipTrack : DJCommand()
    object PreviousTrack : DJCommand()
    object ShuffleQueue : DJCommand()
    object ClearQueue : DJCommand()
    
    data class AddToQueue(val songId: Long) : DJCommand()
    data class PlayNext(val songId: Long) : DJCommand()
    
    data class CreatePlaylist(
        val name: String,
        val durationMinutes: Int? = null,
        val count: Int? = null,
        val genre: String? = null
    ) : DJCommand()

    data class PlaySimilar(val songId: Long? = null) : DJCommand()
    
    data class ChangeMood(val mood: String) : DJCommand()
    data class IncreaseEnergy(val amount: Float = 0.2f) : DJCommand()
    data class DecreaseEnergy(val amount: Float = 0.2f) : DJCommand()
    
    data class FilterYear(val year: Int) : DJCommand()
    data class FilterFormat(val format: String) : DJCommand()
    
    data class ExcludeSong(val songId: Long) : DJCommand()
    data class ExcludeArtist(val artist: String) : DJCommand()
    data class ExcludeGenre(val genre: String) : DJCommand()
    
    object StartDJMode : DJCommand()
    object StopDJMode : DJCommand()
    
    data class AskInformation(val query: String) : DJCommand()
}
