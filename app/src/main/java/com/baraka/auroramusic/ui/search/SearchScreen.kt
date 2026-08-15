package com.baraka.auroramusic.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baraka.auroramusic.ui.library.LibraryViewModel
import com.baraka.auroramusic.ui.library.SongItem
import com.baraka.auroramusic.ui.player.PlayerViewModel

@Composable
fun SearchScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val songs by viewModel.songs.collectAsState()
    val filteredSongs = remember(query, songs) {
        if (query.isEmpty()) emptyList()
        else songs.filter { it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search songs, artists...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredSongs) { song ->
                SongItem(song = song) {
                    playerViewModel.play(song)
                }
            }
        }
    }
}
