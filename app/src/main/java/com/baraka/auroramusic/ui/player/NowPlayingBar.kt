package com.baraka.auroramusic.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.baraka.auroramusic.ui.theme.GlassWhite

@Composable
fun NowPlayingBar(
    viewModel: PlayerViewModel,
    onOpenPlayer: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    if (currentSong != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenPlayer() },
            color = Color.Black.copy(alpha = 0.85f),
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Subtle Progress Line
                if (duration > 0) {
                    val progress = position.toFloat() / duration.toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(2.dp)
                            .align(Alignment.BottomStart)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentSong!!.albumArtUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentSong!!.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            color = Color.White
                        )
                        Text(
                            text = currentSong!!.artist,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    
                    IconButton(onClick = { viewModel.togglePlayback() }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
