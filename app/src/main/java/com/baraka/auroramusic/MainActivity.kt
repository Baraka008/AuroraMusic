package com.baraka.auroramusic

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.baraka.auroramusic.ui.DJScreen
import com.baraka.auroramusic.ui.Screen
import com.baraka.auroramusic.ui.theme.AuroraMusicTheme
import com.baraka.auroramusic.dj.AuroraDJ
import com.baraka.auroramusic.ui.library.LibraryScreen
import com.baraka.auroramusic.ui.search.SearchScreen
import com.baraka.auroramusic.ui.player.NowPlayingBar
import com.baraka.auroramusic.ui.player.NowPlayingScreen
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auroraDJ: AuroraDJ

    @Inject
    lateinit var musicScanner: com.baraka.auroramusic.data.MusicScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuroraMusicTheme {
                val navController = rememberNavController()
                val items = listOf(Screen.Library, Screen.Search, Screen.DJ)
                var showPlayerCard by remember { mutableStateOf(false) }

                val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                val scope = rememberCoroutineScope()
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.values.all { it }
                    if (granted) {
                        scope.launch {
                            musicScanner.scanLocalLibrary(contentResolver)
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    val allGranted = permissionsToRequest.all {
                        checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    }
                    if (allGranted) {
                        musicScanner.scanLocalLibrary(contentResolver)
                    } else {
                        permissionLauncher.launch(permissionsToRequest)
                    }
                }
                
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Column {
                                NowPlayingBar(onOpenPlayer = { showPlayerCard = true })
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                                    val currentRoute = navBackStackEntry?.destination?.route
                                    
                                    items.forEach { screen ->
                                        NavigationBarItem(
                                            icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                                            label = { Text(screen.label) },
                                            selected = currentRoute == screen.route,
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Library.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Library.route) { LibraryScreen() }
                            composable(Screen.Search.route) { SearchScreen() }
                            composable(Screen.DJ.route) { DJScreen(auroraDJ = auroraDJ) }
                        }
                    }

                    AnimatedVisibility(
                        visible = showPlayerCard,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        NowPlayingScreen(onBack = { showPlayerCard = false })
                    }
                }
            }
        }
    }
}
