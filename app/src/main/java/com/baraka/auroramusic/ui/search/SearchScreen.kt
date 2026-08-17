package com.baraka.auroramusic.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baraka.auroramusic.ui.library.LibraryViewModel
import com.baraka.auroramusic.ui.library.SongItem
import com.baraka.auroramusic.ui.player.PlayerViewModel

import com.baraka.auroramusic.ui.theme.SlateBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    playerViewModel: PlayerViewModel,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val songs by viewModel.songs.collectAsState()
    val mostPlayed by viewModel.mostPlayed.collectAsState()
    
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
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SlateBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedLabelColor = SlateBlue
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (query.isEmpty() && mostPlayed.isNotEmpty()) {
            Text("Suggested for you", style = MaterialTheme.typography.titleMedium, color = SlateBlue)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mostPlayed.take(5)) { song ->
                    SongItem(
                        song = song,
                        onFavoriteToggle = { viewModel.toggleFavorite(song) }
                    ) {
                        playerViewModel.play(song, mostPlayed)
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredSongs) { song ->
                    SongItem(
                        song = song,
                        onFavoriteToggle = { viewModel.toggleFavorite(song) }
                    ) {
                        playerViewModel.play(song, filteredSongs)
                    }
                }
            }
        }
    }
}
