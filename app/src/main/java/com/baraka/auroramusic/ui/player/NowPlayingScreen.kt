package com.baraka.auroramusic.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baraka.auroramusic.R
import com.baraka.auroramusic.ui.library.formatDuration
import com.baraka.auroramusic.ui.theme.AmoledBlack
import com.baraka.auroramusic.ui.theme.AccentBlue
import com.baraka.auroramusic.ui.theme.DarkSurface
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val bpm by viewModel.currentBpm.collectAsState()
    val energy by viewModel.currentEnergy.collectAsState()

    if (currentSong == null) return

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AmoledBlack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.15f),
                            AmoledBlack
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NOW PLAYING",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        if (bpm > 0) {
                            Text(
                                text = "${bpm.toInt()} BPM • ${(energy * 100).toInt()}% ENERGY",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentBlue
                            )
                        }
                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Album Art
                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge),
                    color = DarkSurface,
                    shadowElevation = 16.dp
                ) {
                    AsyncImage(
                        model = currentSong!!.albumArtUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_aurora_note)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = currentSong!!.title,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = currentSong!!.artist,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                val sliderValue = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
                Slider(
                    value = sliderValue,
                    onValueChange = { /* Implement seek if needed */ },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = formatDuration(position), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    Text(text = formatDuration(duration), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.toggleShuffle() }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = null,
                            tint = if (shuffleEnabled) AccentBlue else Color.White.copy(alpha = 0.5f)
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.previous() }, modifier = Modifier.size(64.dp)) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                        }
                        
                        Surface(
                            onClick = { viewModel.togglePlayback() },
                            modifier = Modifier.size(80.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = AmoledBlack
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.skip() }, modifier = Modifier.size(64.dp)) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                        }
                    }

                    IconButton(onClick = { viewModel.cycleRepeatMode() }) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = if (repeatMode > 0) AccentBlue else Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
