package com.example.visara.ui.screens.inbox.new_followers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.ui.components.UserAvatar
import com.example.visara.viewmodels.NewFollowerInboxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFollowersInboxScreen(
   modifier: Modifier = Modifier,
   onBack: () -> Unit,
   viewModel: NewFollowerInboxViewModel = hiltViewModel(),
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

   Scaffold(
      topBar = {
         CenterAlignedTopAppBar(
            title = {
                  Text(
                     text = "New follower notifications",
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
         items(uiState.newFollowersNotifications) { notification ->
            Row(
               horizontalArrangement = Arrangement.spacedBy(8.dp),
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
               UserAvatar(avatarLink = notification.senderAvatarUrl, modifier = Modifier.size(70.dp))
               Column {
                  Text(
                     text = notification.senderUsername,
                     fontWeight = FontWeight.Bold
                  )
                  Text(
                     text = notification.message
                  )
               }
            }
         }
      }
   }
}
