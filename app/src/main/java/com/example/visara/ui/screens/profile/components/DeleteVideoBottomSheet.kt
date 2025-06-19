package com.example.visara.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.visara.data.model.VideoModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteVideoBottomSheet(
   modifier: Modifier = Modifier,
   video: VideoModel,
   onDismiss: () -> Unit,
   onSubmit: () -> Unit,
) {
    var isDeleting by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = "Are you sure you want to delete this video?",
                fontWeight = FontWeight.Medium,
            )

            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(300.dp)
                .clip(RoundedCornerShape(20.dp))
                    .aspectRatio(16f / 9f)
            )

            Text(
                text = video.title
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.width(130.dp),
                ) {
                    Text(
                        text = "Cancel"
                    )
                }
                Button(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true
                        onSubmit()
                    },
                    modifier = Modifier.width(130.dp)
                ) {
                    if (!isDeleting) {
                        Text(
                            text = "Delete video",
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
