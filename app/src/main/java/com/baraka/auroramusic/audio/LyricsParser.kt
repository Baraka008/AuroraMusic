package com.baraka.auroramusic.audio

import java.util.regex.Pattern

data class LyricLine(
    val timestamp: Long,
    val text: String
)

object LyricsParser {
    private val timePattern = Pattern.compile("\\[(\\d+):(\\d+)\\.(\\d+)\\]")

    fun parseLrc(content: String): List<LyricLine> {
        val lines = content.lines()
        val result = mutableListOf<LyricLine>()

        for (line in lines) {
            val matcher = timePattern.matcher(line)
            if (matcher.find()) {
                val min = matcher.group(1)?.toLong() ?: 0L
                val sec = matcher.group(2)?.toLong() ?: 0L
                val ms = matcher.group(3)?.toLong() ?: 0L
                val timestamp = (min * 60 * 1000) + (sec * 1000) + (ms * 10) // Ms is often 2 digits in LRC
                
                val text = line.substring(matcher.end()).trim()
                if (text.isNotEmpty()) {
                    result.add(LyricLine(timestamp, text))
                }
            }
        }
        return result.sortedBy { it.timestamp }
    }
}
