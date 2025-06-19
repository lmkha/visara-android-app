package com.example.visara.ui.screens.profile.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.visara.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoMoreActionBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSelectAddVideoToPlaylist: () -> Unit,
    onSelectEditVideo: () -> Unit,
    onSelectDeleteVideo: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Item(
                text = "Add to playlist",
                iconResId = R.drawable.playlist_tab_24px,
                onClick = onSelectAddVideoToPlaylist
            )
            Item(
                text = "Edit",
                iconImageVector = Icons.Default.Edit,
                onClick = onSelectEditVideo,
            )
            Item(
                text = "Delete",
                iconResId = R.drawable.inbox_delete,
                onClick = onSelectDeleteVideo
            )
        }
    }
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconResId: Int? = null,
    iconImageVector: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick)
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
            )
        } else if (iconImageVector != null) {
            Icon(
                imageVector = iconImageVector,
                contentDescription = null,
            )
        }

        Text(
            text = text,
        )
    }
}
