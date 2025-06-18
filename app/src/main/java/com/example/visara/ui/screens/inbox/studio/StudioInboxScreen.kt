package com.example.visara.ui.screens.inbox.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.visara.data.remote.dto.NewVideoProcessedNotificationData
import com.example.visara.ui.components.UserAvatar
import com.example.visara.viewmodels.StudioInboxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioInboxScreen(
   modifier: Modifier = Modifier,
   viewModel: StudioInboxViewModel = hiltViewModel(),
   onBack: () -> Unit,
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

   Scaffold(
      topBar = {
         CenterAlignedTopAppBar(
            title = {
               Text(
                  text = "Studio notification",
                  fontWeight = FontWeight.Bold
               )
            },
            navigationIcon = {
               IconButton(onClick = onBack) {
                  Icon(
                     imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = null,
                  )
               }
            },
            expandedHeight = 50.dp,
            colors = TopAppBarDefaults.topAppBarColors(
               containerColor = MaterialTheme.colorScheme.background,
            ),
         )

      },
      modifier = modifier
   ) { paddingValues ->
      LazyColumn(
         verticalArrangement = Arrangement.spacedBy(8.dp),
         modifier = Modifier.fillMaxSize().padding(paddingValues)
      ) {
         items(uiState.studioNotifications) { notification ->
            val notificationData = notification.data as NewVideoProcessedNotificationData?
            Row(
               horizontalArrangement = Arrangement.spacedBy(8.dp),
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(70.dp)
                  .padding(horizontal = 4.dp)
            ) {
               Column(modifier = Modifier.weight(1f)) {
                  Text(
                     text = notification.message,
                     fontWeight = FontWeight.Medium
                  )
               }
               Box(
                  contentAlignment = Alignment.CenterEnd,
                  modifier = Modifier
                     .width(100.dp)
                     .clip(RoundedCornerShape(10.dp))
                     .background(Color.Black)
               ) {
                  AsyncImage(
                     model = notificationData?.thumbnailUrl,
                     contentDescription = null,
                     contentScale = ContentScale.Fit,
                  )
               }
            }
         }
      }
   }
}
