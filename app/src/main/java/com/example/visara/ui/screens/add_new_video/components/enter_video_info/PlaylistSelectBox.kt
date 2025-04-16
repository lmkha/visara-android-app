package com.example.visara.ui.screens.add_new_video.components.enter_video_info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visara.R

@Composable
fun AddVideoToPlaylistsBox (
    modifier: Modifier = Modifier,
    currentSelectedPlaylists: List<String>,
    onBack: () -> Unit = {},
    onSelectFinished: (value: List<String>) -> Unit = { _ -> },
) {
    val playlists = listOf<String>("music", "football", "barca", "champion league")
    var selectedPlaylists by remember { mutableStateOf<List<String>>(currentSelectedPlaylists) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        // Header: Back button and finish button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
            Text(
                text = "Add video to playlists",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { onSelectFinished(selectedPlaylists) },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                Text("OK")
            }
        }
        Spacer(Modifier.height(16.dp))

        // List of playlist
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(playlists.size) {index ->
                val playlist = playlists[index]
                Row(
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clickable {
                            selectedPlaylists = if (selectedPlaylists.contains(playlist)) {
                                selectedPlaylists - playlist
                            } else {
                                selectedPlaylists + playlist
                            }
                        }
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Checkbox(
                        checked = selectedPlaylists.contains(playlist),
                        onCheckedChange = {
                            selectedPlaylists = if (selectedPlaylists.contains(playlist)) {
                                selectedPlaylists - playlist
                            } else {
                                selectedPlaylists + playlist
                            }
                        }
                    )

                    Text(
                        text = playlist,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.public_24px),
                        contentDescription = null
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
