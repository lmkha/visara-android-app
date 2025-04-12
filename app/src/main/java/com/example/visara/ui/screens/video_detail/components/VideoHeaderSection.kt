package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VideoHeaderSection() {
    Column(
        modifier = Modifier
            .clickable {

            }
        ,
    ) {
        Text(
            text = "FC Barcelona 1 vs 1 Betis | Laliga 2024/25 MD30",
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("22,007 views")
            Text("8h ago")
            Text("#BLVAnhQuan")
            Text("...more")
        }

    }
    Spacer(Modifier.height(16.dp).fillMaxWidth())
}
