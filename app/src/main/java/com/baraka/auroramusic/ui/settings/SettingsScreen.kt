package com.baraka.auroramusic.ui.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val djSettings by viewModel.djSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsSection(title = "Aurora DJ") {
                djSettings?.let { settings ->
                    SwitchSetting(
                        label = "Voice Commentary",
                        description = "Enable AI DJ voice during song transitions",
                        checked = settings.voiceEnabled,
                        onCheckedChange = { viewModel.updateVoiceEnabled(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Commentary Frequency", style = MaterialTheme.typography.bodyMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("LOW", "NORMAL", "HIGH").forEach { freq ->
                            FilterChip(
                                selected = settings.commentaryFrequency == freq,
                                onClick = { viewModel.updateCommentaryFrequency(freq) },
                                label = { Text(freq) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Discovery Level (${(settings.discoveryLevel * 100).toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = settings.discoveryLevel,
                        onValueChange = { viewModel.updateDiscoveryLevel(it) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            SettingsSection(title = "Audio Equalizer") {
                val presets = listOf("Flat", "Rock", "Pop", "Jazz", "Classic", "Dance", "HipHop")
                var selectedPreset by remember { mutableIntStateOf(0) }
                
                Text("Preset", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEachIndexed { index, name ->
                        FilterChip(
                            selected = selectedPreset == index,
                            onClick = { 
                                selectedPreset = index
                                viewModel.setEQPreset(index)
                            },
                            label = { Text(name) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                var bassBoost by remember { mutableFloatStateOf(0f) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bass Boost", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = bassBoost,
                        onValueChange = { 
                            bassBoost = it
                            viewModel.setBassBoost(it) 
                        },
                        valueRange = 0f..15f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${bassBoost.toInt()} dB", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                var reverbLevel by remember { mutableFloatStateOf(0f) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reverb", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = reverbLevel,
                        onValueChange = { 
                            reverbLevel = it
                            viewModel.setReverbLevel(it) 
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(reverbLevel * 100).toInt()}%", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                var virtualizerLevel by remember { mutableFloatStateOf(0f) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Virtualizer", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = virtualizerLevel,
                        onValueChange = { 
                            virtualizerLevel = it
                            viewModel.setVirtualizerLevel(it) 
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${(virtualizerLevel * 100).toInt()}%", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                val bands = listOf("31Hz", "62Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz")
                bands.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelMedium)
                        var gain by remember { mutableFloatStateOf(0f) }
                        Slider(
                            value = gain,
                            onValueChange = { 
                                gain = it
                                viewModel.setEQBand(index, it) 
                            },
                            valueRange = -12f..12f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${gain.toInt()} dB", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            SettingsSection(title = "Appearance") {
                val amoledMode by viewModel.amoledMode.collectAsState()
                SwitchSetting(
                    label = "AMOLED Black",
                    description = "Use pure black for backgrounds to save battery on OLED screens",
                    checked = amoledMode,
                    onCheckedChange = { viewModel.updateAmoledMode(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
fun SwitchSetting(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
