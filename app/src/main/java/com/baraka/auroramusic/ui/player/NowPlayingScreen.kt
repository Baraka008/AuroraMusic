package com.baraka.auroramusic.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.baraka.auroramusic.R
import com.baraka.auroramusic.ui.library.formatDuration
import com.baraka.auroramusic.ui.theme.AmoledBlack
import com.baraka.auroramusic.ui.theme.DarkSurface
import com.baraka.auroramusic.ui.theme.DeepIndigo
import com.baraka.auroramusic.ui.theme.SlateBlue
import com.baraka.auroramusic.ui.theme.GlassWhite
import com.baraka.auroramusic.ui.theme.GlassWhiteLight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.AsyncImage
import com.baraka.auroramusic.audio.NativeAudioEngine
import com.baraka.auroramusic.audio.DeviceType
import com.baraka.auroramusic.audio.DeviceInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    nativeEngine: NativeAudioEngine,
    windowSizeClass: WindowSizeClass,
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
    val currentDevice by viewModel.currentDevice.collectAsState()
    val currentLyrics by viewModel.currentLyrics.collectAsState()
    val isFetchingLyrics by viewModel.isFetchingLyrics.collectAsState()
    
    var showLyrics by remember { mutableStateOf(false) }

    if (currentSong == null) return

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AmoledBlack
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepIndigo.copy(alpha = 0.3f),
                            AmoledBlack
                        ),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        ) {
            val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
            val isShort = maxHeight < 600.dp
            
            if (isExpanded) {
                LandscapeLayout(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    shuffleEnabled = shuffleEnabled,
                    repeatMode = repeatMode,
                    position = position,
                    duration = duration,
                    bpm = bpm,
                    energy = energy,
                    currentDevice = currentDevice,
                    nativeEngine = nativeEngine,
                    viewModel = viewModel,
                    onBack = onBack
                )
            } else {
                PortraitLayout(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    shuffleEnabled = shuffleEnabled,
                    repeatMode = repeatMode,
                    position = position,
                    duration = duration,
                    bpm = bpm,
                    energy = energy,
                    currentDevice = currentDevice,
                    isShort = isShort,
                    showLyrics = showLyrics,
                    onToggleLyrics = { showLyrics = !showLyrics },
                    currentLyrics = currentLyrics,
                    isFetchingLyrics = isFetchingLyrics,
                    nativeEngine = nativeEngine,
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
fun PortraitLayout(
    song: com.baraka.auroramusic.data.entities.Song,
    isPlaying: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    position: Long,
    duration: Long,
    bpm: Float,
    energy: Float,
    currentDevice: DeviceInfo,
    isShort: Boolean,
    showLyrics: Boolean,
    onToggleLyrics: () -> Unit,
    currentLyrics: String?,
    isFetchingLyrics: Boolean,
    nativeEngine: NativeAudioEngine,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerHeader(bpm, energy, viewModel, onBack)

        Spacer(modifier = Modifier.weight(0.5f))

        if (showLyrics) {
            Box(
                modifier = Modifier
                    .weight(if (isShort) 4f else 6f)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(GlassWhiteLight)
                    .clickable { onToggleLyrics() },
                contentAlignment = Alignment.Center
            ) {
                if (isFetchingLyrics) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    LyricsView(
                        lyrics = currentLyrics ?: "[00:00.00]${song.title}\n[00:02.00]${song.artist}",
                        currentPosition = position
                    )
                }
            }
        } else {
            // Album Art
            Box(
                modifier = Modifier
                    .weight(if (isShort) 4f else 6f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                if (dragAmount > 30) { // More sensitive
                                    viewModel.previous()
                                } else if (dragAmount < -30) {
                                    viewModel.skip()
                                }
                            }
                        )
                    }
                    .clickable { onToggleLyrics() },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight()
                        .widthIn(max = 450.dp)
                        .clip(MaterialTheme.shapes.extraLarge),
                    color = DarkSurface,
                    shadowElevation = 16.dp
                ) {
                    AsyncImage(
                        model = song.albumArtUri,
                        contentDescription = "Album Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_aurora_note)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        VisualizerView(
            nativeEngine = nativeEngine,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isShort) 40.dp else 60.dp)
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.weight(0.5f))

        SongInfo(song, viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        SeekSlider(position, duration, viewModel)
        
        Spacer(modifier = Modifier.weight(1f))

        PlaybackControls(isPlaying, shuffleEnabled, repeatMode, viewModel)
        
        Spacer(modifier = Modifier.weight(0.5f))
        
        DeviceIndicator(currentDevice)
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LandscapeLayout(
    song: com.baraka.auroramusic.data.entities.Song,
    isPlaying: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    position: Long,
    duration: Long,
    bpm: Float,
    energy: Float,
    currentDevice: DeviceInfo,
    nativeEngine: NativeAudioEngine,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        // Left side: Album Art
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight(0.85f)
                    .clip(MaterialTheme.shapes.extraLarge),
                color = DarkSurface,
                shadowElevation = 16.dp
            ) {
                AsyncImage(
                    model = song.albumArtUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_aurora_note)
                )
            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Right side: Controls and Info
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            PlayerHeader(bpm, energy, viewModel, onBack)
            Spacer(modifier = Modifier.weight(1f))
            SongInfo(song, viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            VisualizerView(nativeEngine = nativeEngine, modifier = Modifier.height(60.dp))
            Spacer(modifier = Modifier.height(16.dp))
            SeekSlider(position, duration, viewModel)
            Spacer(modifier = Modifier.weight(1f))
            PlaybackControls(isPlaying, shuffleEnabled, repeatMode, viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            DeviceIndicator(currentDevice)
        }
    }
}

@Composable
fun PlayerHeader(
    bpm: Float,
    energy: Float,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown, 
                contentDescription = "Collapse Player", 
                tint = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.now_playing),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.7f)
            )
            if (bpm > 0) {
                Text(
                    text = "${bpm.toInt()} BPM • ${(energy * 100).toInt()}% ENERGY",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateBlue
                )
            }
        }
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, 
                    contentDescription = "More Options", 
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sleep Timer (30 min)") },
                    onClick = {
                        viewModel.setSleepTimer(30)
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Playback Speed (1.5x)") },
                    onClick = {
                        viewModel.setPlaybackSpeed(1.5f)
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Reset Audio") },
                    onClick = {
                        viewModel.setPlaybackSpeed(1.0f)
                        viewModel.setPlaybackPitch(1.0f)
                        showMenu = false
                    }
                )
            }
        }
    }
}

@Composable
fun SongInfo(
    song: com.baraka.auroramusic.data.entities.Song,
    viewModel: PlayerViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
        IconButton(onClick = { viewModel.toggleFavorite() }) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (song.isFavorite) SlateBlue else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SeekSlider(position: Long, duration: Long, viewModel: PlayerViewModel) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val sliderValue = if (isDragging) {
        sliderPosition
    } else {
        if (duration > 0) position.toFloat() / duration.toFloat() else 0f
    }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Slider(
            value = sliderValue.coerceIn(0f, 1f),
            onValueChange = {
                isDragging = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                viewModel.seekTo((sliderPosition * duration).toLong())
                isDragging = false
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = SlateBlue,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatDuration(position), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
            Text(text = formatDuration(duration), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun PlaybackControls(isPlaying: Boolean, shuffleEnabled: Boolean, repeatMode: Int, viewModel: PlayerViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { viewModel.toggleShuffle() }) {
            val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = if (shuffleEnabled) "Disable Shuffle" else "Enable Shuffle",
                tint = if (shuffleEnabled) SlateBlue else Color.White.copy(alpha = 0.5f)
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.previous() }, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious, 
                    contentDescription = "Previous Track", 
                    modifier = Modifier.size(36.dp), 
                    tint = Color.White
                )
            }
            
            Surface(
                onClick = { viewModel.togglePlayback() },
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val isPlaying by viewModel.isPlaying.collectAsState()
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(48.dp),
                        tint = AmoledBlack
                    )
                }
            }

            IconButton(onClick = { viewModel.skip() }, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = Icons.Default.SkipNext, 
                    contentDescription = "Skip Track", 
                    modifier = Modifier.size(36.dp), 
                    tint = Color.White
                )
            }
        }

        IconButton(onClick = { viewModel.cycleRepeatMode() }) {
            val repeatMode by viewModel.repeatMode.collectAsState()
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = "Cycle Repeat Mode",
                tint = if (repeatMode > 0) SlateBlue else Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DeviceIndicator(device: DeviceInfo) {
    val icon = when (device.type) {
        DeviceType.BLUETOOTH -> Icons.Default.Bluetooth
        DeviceType.WIRED -> Icons.Default.Headphones
        DeviceType.SPEAKER -> Icons.Default.Speaker
        DeviceType.OTHER -> Icons.Default.Cast
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(GlassWhiteLight)
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Device Type",
            tint = SlateBlue,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.playing_on, device.name),
            style = MaterialTheme.typography.labelSmall,
            color = SlateBlue
        )
    }
}
