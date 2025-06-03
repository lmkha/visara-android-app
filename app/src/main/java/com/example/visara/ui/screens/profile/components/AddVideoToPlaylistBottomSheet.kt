package com.example.visara.ui.screens.profile.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.data.model.PlaylistModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVideoToPlaylistBottomSheet(
    modifier: Modifier = Modifier,
    playlists: List<PlaylistModel>,
    onDismissRequest: () -> Unit,
    onSelectAddNewPlaylist: () -> Unit,
    onFinish: (playlistIds: List<String>) -> Unit,
) {
    var selectedItemIdSet by remember { mutableStateOf(setOf<String>()) }
    var allPlaylistMap by remember(playlists) {
        val value = mutableMapOf<PlaylistModel, Boolean>()
        playlists.forEach { playlist ->
            value[playlist] = selectedItemIdSet.contains(playlist.id)
        }
        mutableStateOf(value.toMap())
    }
    var isProcessing by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add video to ...")
                TextButton(onClick = onSelectAddNewPlaylist) { Text("New playlist +") }
            }
            LazyColumn(
                modifier = Modifier
                    .requiredHeightIn(
                        min = 0.dp,
                        max = 200.dp
                    )
            ) {
                items(allPlaylistMap.entries.toList()) { item ->
                    PlaylistItem(
                        text = item.key.name,
                        icon = R.drawable.lock_24px,
                        selected = item.value,
                        onSelect = {
                            allPlaylistMap = allPlaylistMap.toMutableMap().apply {
                                val newSet = selectedItemIdSet.toMutableSet()
                                if (item.value) {
                                    newSet.remove(item.key.id)
                                } else {
                                    newSet.add(item.key.id)
                                }
                                selectedItemIdSet = newSet
                                this[item.key] = !item.value
                            }
                        }
                    )
                }
            }
            OutlinedButton(
                onClick = {
                    isProcessing = true
                    onFinish(selectedItemIdSet.toList())
                },
                enabled = selectedItemIdSet.isNotEmpty(),
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            ) {
                if (!isProcessing) {
                    Text(
                        text = "Finish",
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistItem(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes icon: Int,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onSelect)
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onSelect() }
            )
            Text(
                text = text,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
            )
        }
        HorizontalDivider()
    }
}
