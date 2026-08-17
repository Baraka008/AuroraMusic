package com.baraka.auroramusic.data.dao

import androidx.room.*
import com.baraka.auroramusic.data.entities.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT uri FROM songs")
    suspend fun getAllUris(): List<String>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): Song?

    @Query("SELECT * FROM songs WHERE uri = :uri")
    suspend fun getSongByUri(uri: String): Song?

    @Query("SELECT * FROM songs WHERE artist = :artist")
    suspend fun getSongsByArtist(artist: String): List<Song>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(song: Song): Long

    @Update
    suspend fun updateSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)
    
    @Query("SELECT * FROM songs WHERE djExcluded = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomSongs(limit: Int): List<Song>

    @Query("SELECT * FROM songs WHERE genre LIKE :genre AND djExcluded = 0")
    suspend fun getSongsByGenre(genre: String): List<Song>

    @Query("SELECT * FROM songs WHERE year = :year AND djExcluded = 0")
    suspend fun getSongsByYear(year: Int): List<Song>

    @Query("SELECT * FROM songs WHERE djExcluded = 0 ORDER BY dateAdded DESC LIMIT :limit")
    suspend fun getRecentlyAdded(limit: Int): List<Song>

    @Query("SELECT * FROM songs WHERE djExcluded = 0 ORDER BY playCount DESC LIMIT :limit")
    suspend fun getMostPlayed(limit: Int): List<Song>

    @Query("SELECT * FROM songs WHERE playCount = 0 AND djExcluded = 0")
    suspend fun getUnheardSongs(): List<Song>

    @Query("SELECT DISTINCT artist FROM songs WHERE djExcluded = 0 ORDER BY artist ASC")
    fun getDistinctArtists(): Flow<List<String>>

    @Query("SELECT DISTINCT album FROM songs WHERE djExcluded = 0 ORDER BY album ASC")
    fun getDistinctAlbums(): Flow<List<String>>

    @Query("SELECT DISTINCT genre FROM songs WHERE djExcluded = 0 ORDER BY genre ASC")
    fun getDistinctGenres(): Flow<List<String>>

    @Query("SELECT * FROM songs WHERE artist = :artist ORDER BY title ASC")
    fun getSongsByArtistFlow(artist: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE album = :album ORDER BY title ASC")
    fun getSongsByAlbumFlow(album: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE genre = :genre ORDER BY title ASC")
    fun getSongsByGenreFlow(genre: String): Flow<List<Song>>

    @Query("SELECT DISTINCT folderPath FROM songs WHERE djExcluded = 0 ORDER BY folderPath ASC")
    fun getDistinctFolders(): Flow<List<String>>

    @Query("SELECT * FROM songs WHERE folderPath = :folderPath ORDER BY title ASC")
    fun getSongsInFolder(folderPath: String): Flow<List<Song>>

    @Query("""
        SELECT s.* FROM songs s 
        JOIN music_features f ON s.id = f.songId 
        WHERE f.energy BETWEEN :minEnergy AND :maxEnergy 
        AND s.djExcluded = 0
    """)
    suspend fun getSongsByEnergyRange(minEnergy: Float, maxEnergy: Float): List<Song>
}
