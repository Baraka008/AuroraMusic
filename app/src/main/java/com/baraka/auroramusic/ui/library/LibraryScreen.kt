package com.baraka.auroramusic.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.baraka.auroramusic.R
import com.baraka.auroramusic.data.entities.Playlist
import com.baraka.auroramusic.data.entities.Song
import com.baraka.auroramusic.ui.player.PlayerViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    playerViewModel: PlayerViewModel,
    onNavigateToSettings: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_songs),
        stringResource(R.string.tab_artists),
        stringResource(R.string.tab_albums),
        stringResource(R.string.tab_genres),
        stringResource(R.string.tab_folders),
        stringResource(R.string.tab_playlists),
        stringResource(R.string.tab_favorites)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_library), style = MaterialTheme.typography.headlineLarge) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = MaterialTheme.typography.titleSmall) }
                    )
                }
            }

            if (songs.isEmpty()) {
                EmptyLibraryState { /* Handle re-scan or initial scan */ }
            } else {
                when (selectedTab) {
                    0 -> SongList(songs, playerViewModel, viewModel)
                    1 -> SimpleList(viewModel.artists.collectAsState().value, stringResource(R.string.tab_artists))
                    2 -> SimpleList(viewModel.albums.collectAsState().value, stringResource(R.string.tab_albums))
                    3 -> SimpleList(viewModel.genres.collectAsState().value, stringResource(R.string.tab_genres))
                    4 -> SimpleList(viewModel.folders.collectAsState().value, stringResource(R.string.tab_folders))
                    5 -> PlaylistList(playlists, viewModel, playerViewModel)
                    6 -> SongList(favorites, playerViewModel, viewModel)
                }
            }
        }
    }
}

@Composable
fun EmptyLibraryState(onScan: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_aurora_note),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.empty_library),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onScan) {
                Text(stringResource(R.string.scan_music))
            }
        }
    }
}

@Composable
fun SongList(
    songs: List<Song>,
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = songs,
            key = { it.id }
        ) { song ->
            SongItem(
                song = song,
                onFavoriteToggle = { libraryViewModel.toggleFavorite(song) }
            ) {
                playerViewModel.play(song, songs)
            }
        }
    }
}

@Composable
fun SimpleList(items: List<String>, type: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(items) { item ->
            ListItem(
                headlineContent = { Text(item) },
                supportingContent = { Text(type) },
                modifier = Modifier.fillMaxWidth().clickable { /* Navigate to detail */ }
            )
        }
    }
}

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    viewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel
) {
    val recentlyAdded by viewModel.recentlyAdded.collectAsState()
    val mostPlayed by viewModel.mostPlayed.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            SmartPlaylistEntry("Recently Added", recentlyAdded) {
                // Play recently added
            }
        }
        item {
            SmartPlaylistEntry("Most Played", mostPlayed) {
                // Play most played
            }
        }
        
        items(playlists) { playlist ->
            ListItem(
                headlineContent = { Text(playlist.name) },
                supportingContent = { Text(playlist.description) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SmartPlaylistEntry(title: String, songs: List<Song>, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text("${songs.size} songs") },
        leadingContent = {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SongItem(
    song: Song,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.albumArtUri,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f)
                )
            }

            Text(
                text = formatDuration(song.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
