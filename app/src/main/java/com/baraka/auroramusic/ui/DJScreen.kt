package com.baraka.auroramusic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.baraka.auroramusic.dj.AuroraDJ

import androidx.compose.ui.res.painterResource
import com.baraka.auroramusic.R

@Composable
fun DJScreen(
    auroraDJ: AuroraDJ,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_dj),
            contentDescription = "DJ Icon",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AURORA DJ",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Talk to AURORA DJ") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                IconButton(onClick = { /* startListening */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic),
                        contentDescription = "Voice Input"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    auroraDJ.processInput(textInput)
                    textInput = ""
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send Command"
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DJActionButton("Chill", R.drawable.ic_aurora_note) { 
                auroraDJ.processInput("Play something chill") 
            }
            DJActionButton("Energy", R.drawable.ic_energy) { 
                auroraDJ.processInput("Play something energetic") 
            }
            DJActionButton("Mood", R.drawable.ic_mood) { 
                auroraDJ.processInput("Surprise me") 
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DJActionButton(label: String, iconRes: Int, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}
