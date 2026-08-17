package com.baraka.auroramusic.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.Song
import com.baraka.auroramusic.dj.MusicFeatureAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicScanner @Inject constructor(
    private val songDao: SongDao,
    private val featureAnalyzer: MusicFeatureAnalyzer
) {
    suspend fun scanLocalLibrary(contentResolver: ContentResolver) = withContext(Dispatchers.IO) {
        val existingUris = songDao.getAllUris().toSet()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.ALBUM_ID
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val yearIdx = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
            val albumIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val songUri = cursor.getString(dataIdx)
                val folderPath = songUri.substringBeforeLast("/", "")
                
                // Check if song already exists
                if (existingUris.contains(songUri)) continue

                val albumId = cursor.getLong(albumIdIdx)
                val artUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                ).toString()

                val song = Song(
                    title = cursor.getString(titleIdx),
                    artist = cursor.getString(artistIdx),
                    album = cursor.getString(albumIdx),
                    duration = cursor.getLong(durationIdx),
                    uri = songUri,
                    folderPath = folderPath,
                    albumArtUri = artUri,
                    year = if (yearIdx != -1) cursor.getInt(yearIdx) else null,
                    genre = "Unknown"
                )
                val id = songDao.insertSong(song)
                featureAnalyzer.analyzeSong(song.copy(id = id))
            }
        }
    }
}
