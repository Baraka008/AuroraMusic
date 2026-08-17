package com.baraka.auroramusic.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.baraka.auroramusic.audio.NativeAudioEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun VisualizerView(
    nativeEngine: NativeAudioEngine,
    modifier: Modifier = Modifier
) {
    var fftData by remember { mutableStateOf(floatArrayOf()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            val data = nativeEngine.getFFTData()
            if (data != null) {
                fftData = data
            }
            delay(16) // ~60fps
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val width = size.width
        val height = size.height
        
        if (fftData.isEmpty()) {
            // Draw a flat line if no data
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = androidx.compose.ui.geometry.Offset(0f, height / 2),
                end = androidx.compose.ui.geometry.Offset(width, height / 2),
                strokeWidth = 2f
            )
            return@Canvas
        }

        val barWidth = width / fftData.size.coerceAtLeast(1)

        fftData.forEachIndexed { index, magnitude ->
            // Scale magnitude for better visibility
            val barHeight = (magnitude * 2.0f).coerceIn(0f, 1f) * height
            drawRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = androidx.compose.ui.geometry.Offset(index * barWidth, height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth - 2f, barHeight.coerceAtLeast(2f))
            )
        }
    }
}
