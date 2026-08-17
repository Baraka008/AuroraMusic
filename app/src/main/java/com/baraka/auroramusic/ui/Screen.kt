package com.baraka.auroramusic.ui

sealed class Screen(val route: String, val label: String, val icon: Int) {
    object Library : Screen("library", "Library", com.baraka.auroramusic.R.drawable.ic_aurora_note)
    object Search : Screen("search", "Search", com.baraka.auroramusic.R.drawable.ic_send)
    object DJ : Screen("dj", "Aurora DJ", com.baraka.auroramusic.R.drawable.ic_dj)
    object Settings : Screen("settings", "Settings", com.baraka.auroramusic.R.drawable.ic_aurora_note) // Use a better icon if available, ic_aurora_note for now
}
