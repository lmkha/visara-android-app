package com.example.visara.ui.screens.edit_profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.visara.ui.components.UserAvatar
import com.example.visara.viewmodels.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
   modifier: Modifier = Modifier,
   viewModel: EditProfileViewModel = hiltViewModel(),
   onBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var openEditFullNameBox by remember { mutableStateOf(false) }
    var openEditBioBox by remember { mutableStateOf(false) }

    Box {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Edit Profile",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        containerColor = MaterialTheme.colorScheme.background,
                    )
                )
            },
            modifier = modifier.background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 8.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    ) {
                        UserAvatar(avatarLink = uiState.currentUser?.networkAvatarUrl)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.photo_camera_24px),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Text("Change avatar")
                }

                Text(
                    text = "About you",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                        .clickable { openEditFullNameBox = true }
                ) {
                    Text(
                        text = "Full name",
                        fontWeight = FontWeight.Bold,
                    )

                    Row {
                        Text(
                            text = uiState.currentUser?.fullName ?: "full name",
                            fontWeight = FontWeight.Normal,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                        .clickable { openEditBioBox = true }
                ) {
                    Text(
                        text = "Bio",
                        fontWeight = FontWeight.Bold,
                    )

                    Row {
                        Text(
                            text = uiState.currentUser?.bio.let { bio ->
                                if (bio.isNullOrBlank()) "No bio yet" else bio
                            },
                            fontWeight = FontWeight.Normal,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }


        AnimatedVisibility(
            visible = openEditFullNameBox,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
        ) {
            EditFullNameBox(
                currentFullName = uiState.currentUser?.fullName ?: "",
                onBack = { openEditFullNameBox = false },
                onSave = { newFullName -> viewModel.updateFullName(newFullName) },
                eventFlow = viewModel.eventFlow
            )
        }


        AnimatedVisibility(
            visible = openEditBioBox,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
        ) {
            EditBioBox(
                currentBio = uiState.currentUser?.bio ?: "",
                onBack = { openEditBioBox = false },
                onSave = { newBio -> viewModel.updateBio(newBio) },
                eventFlow = viewModel.eventFlow,
            )
        }
    }
}
