package com.baraka.auroramusic.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baraka.auroramusic.audio.LyricLine
import com.baraka.auroramusic.audio.LyricsParser
import kotlinx.coroutines.launch

@Composable
fun LyricsView(
    lyrics: String,
    currentPosition: Long,
    modifier: Modifier = Modifier
) {
    val parsedLyrics = remember(lyrics) { LyricsParser.parseLrc(lyrics) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val currentLineIndex = remember(currentPosition, parsedLyrics) {
        parsedLyrics.indexOfLast { it.timestamp <= currentPosition }.coerceAtLeast(0)
    }

    LaunchedEffect(currentLineIndex) {
        if (parsedLyrics.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(currentLineIndex, scrollOffset = -200)
            }
        }
    }

    if (parsedLyrics.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No lyrics available", color = Color.White.copy(alpha = 0.5f))
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(parsedLyrics) { index, line ->
                val isCurrent = index == currentLineIndex
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = if (isCurrent) 28.sp else 22.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 24.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
