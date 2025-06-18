package com.example.visara.ui.screens.inbox.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.R
import com.example.visara.viewmodels.InboxListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxListScreen(
   modifier: Modifier = Modifier,
   viewModel: InboxListViewModel = hiltViewModel(),
   bottomNavBar: @Composable () -> Unit,
   onOpenActivityInbox: () -> Unit,
   onOpenNewFollowersInbox: () -> Unit,
   openStudioInbox: () -> Unit,
   onOpenSystemNotificationInbox: () -> Unit,
) {

   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

   Scaffold(
      modifier = modifier.fillMaxSize(),
      topBar = {
         CenterAlignedTopAppBar(
            title = {
               Row(
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = {}
                     )
               ) {
                  Text(
                     text = "Inbox",
                     fontWeight = FontWeight.Bold,
                  )
                  Box(
                     modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color.LightGray.copy(alpha = 0.5f))
                  ) {
                     Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                     ) {
                        Box(
                           modifier = Modifier
                              .size(12.dp)
                              .clip(CircleShape)
                              .background(color = Color.Green)
                        )

                        Icon(
                           imageVector = Icons.Default.ArrowDropDown,
                           contentDescription = null,
                        )
                     }
                  }
               }
            },
            navigationIcon = {
               IconButton(
                  onClick = {},
               ) {
                  Icon(
                     painter = painterResource(id = R.drawable.group_add_24px),
                     contentDescription = null,
                  )
               }
            },
            actions = {
               IconButton(
                  onClick = { },
               ) {
                  Icon(
                     imageVector = Icons.Default.Search,
                     contentDescription = null,
                  )
               }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
               containerColor = MaterialTheme.colorScheme.background,
            ),
         )
      },
      bottomBar = { bottomNavBar() },
   ) { innerPadding->
      LazyColumn(
         verticalArrangement = Arrangement.spacedBy(8.dp),
         modifier = Modifier.padding(innerPadding)
      ) {
         // New Followers
         item {
            NewFollowerInboxListItem(
               notifications = uiState.newFollowersNotifications,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(70.dp)
                  .padding(horizontal = 8.dp)
                  .clip(RoundedCornerShape(30.dp))
                  .clickable { onOpenNewFollowersInbox() }
            )
         }
         // Activity
         item {
            ActivityInboxListItem(
               notifications = uiState.activityNotifications,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(70.dp)
                  .padding(horizontal = 8.dp)
                  .clip(RoundedCornerShape(30.dp))
                  .clickable { onOpenActivityInbox() }
            )
         }
         // Studio
         item {
            StudioInboxListItem(
               notifications = uiState.studioNotifications,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(70.dp)
                  .padding(horizontal = 8.dp)
                  .clip(RoundedCornerShape(30.dp))
                  .clickable { openStudioInbox() }
            )
         }
         // System notification
         item {
            SystemNotificationInboxListItem(
               notifications = uiState.systemNotifications,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(70.dp)
                  .padding(horizontal = 8.dp)
                  .clip(RoundedCornerShape(30.dp))
                  .clickable { onOpenSystemNotificationInbox() }
            )
         }
      }
   }
}
