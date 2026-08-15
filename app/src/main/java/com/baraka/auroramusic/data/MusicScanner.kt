package com.baraka.auroramusic.data

import android.content.ContentResolver
import android.provider.MediaStore
import com.baraka.auroramusic.data.dao.SongDao
import com.baraka.auroramusic.data.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicScanner @Inject constructor(
    private val songDao: SongDao
) {
    suspend fun scanLocalLibrary(contentResolver: ContentResolver) = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.GENRE
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val yearIdx = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)

            while (cursor.moveToNext()) {
                val song = Song(
                    title = cursor.getString(titleIdx),
                    artist = cursor.getString(artistIdx),
                    album = cursor.getString(albumIdx),
                    duration = cursor.getLong(durationIdx),
                    uri = cursor.getString(dataIdx),
                    year = if (yearIdx != -1) cursor.getInt(yearIdx) else null,
                    genre = "Unknown" // Genre is harder to get via MediaStore projection on some versions
                )
                songDao.insertSong(song)
            }
        }
    }
}
