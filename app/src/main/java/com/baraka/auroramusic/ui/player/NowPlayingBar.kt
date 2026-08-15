package com.baraka.auroramusic.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NowPlayingBar(
    viewModel: PlayerViewModel,
    onOpenPlayer: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    if (currentSong != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { onOpenPlayer() },
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = currentSong!!.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                    Text(text = currentSong!!.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
                
                IconButton(onClick = { viewModel.togglePlayback() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
