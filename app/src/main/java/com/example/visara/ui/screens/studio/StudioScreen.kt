package com.example.visara.ui.screens.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.viewmodels.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    modifier: Modifier = Modifier,
    viewModel: StudioViewModel = hiltViewModel(),
    initialSelectedTag: StudioSelectedTag = StudioSelectedTag.PROCESSING,
    onNavigateToAddNewVideoScreen: (localDraftVideoId: Long) -> Unit,
    onBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTag by remember(initialSelectedTag) { mutableStateOf(initialSelectedTag) }

    Scaffold(
        modifier = modifier,
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
                        text = "Studio",
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
    ) { innerPaddingValue ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(innerPaddingValue)
                    .padding(horizontal = 4.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
               item {
                   Row(
                       horizontalArrangement = Arrangement.spacedBy(8.dp),
                       modifier = Modifier
                           .fillMaxWidth()
                           .horizontalScroll(rememberScrollState())
                   ) {
                       Tag(
                           text = "Active",
                           onClick = { selectedTag = StudioSelectedTag.ACTIVE },
                           selected = selectedTag == StudioSelectedTag.ACTIVE,
                           iconColor = Color.Green,
                       )
                       Tag(
                           text = "Processing",
                           onClick = { selectedTag = StudioSelectedTag.PROCESSING },
                           selected = selectedTag == StudioSelectedTag.PROCESSING,
                           iconColor = Color.Blue,
                       )
                       Tag(
                           text = "Uploading",
                           onClick = { selectedTag = StudioSelectedTag.UPLOADING },
                           selected = selectedTag == StudioSelectedTag.UPLOADING,
                           iconColor = Color.Magenta,
                       )
                       Tag(
                           text = "Draft",
                           onClick = { selectedTag = StudioSelectedTag.DRAFT },
                           selected = selectedTag == StudioSelectedTag.DRAFT,
                           iconColor = Color.Cyan,
                       )
                       Tag(
                           text = "Pending ReUpload",
                           onClick = { selectedTag = StudioSelectedTag.PENDING_RE_UPLOAD },
                           selected = selectedTag == StudioSelectedTag.PENDING_RE_UPLOAD,
                           iconColor = Color.Yellow,
                       )
                   }
               }

                when (selectedTag) {
                    StudioSelectedTag.ACTIVE -> {
                        items(uiState.activeVideos) { video ->
                            StudioVideoItem(
                                video = video,
                                onVideoSelected = {  }
                            )
                        }
                    }
                    StudioSelectedTag.PROCESSING -> {
                        items(uiState.processingVideos) { video ->
                            StudioVideoItem(
                                video = video,
                                onVideoSelected = {},
                                modifier = Modifier
                            )
                        }
                    }
                    StudioSelectedTag.UPLOADING -> {
                        items(uiState.uploadingVideos) { video ->
                            StudioVideoItem(
                                video = video,
                                onVideoSelected = {},
                                modifier = Modifier
                            )
                        }
                    }
                    StudioSelectedTag.DRAFT -> {
                        items(uiState.draftVideos) { video ->
                            StudioVideoItem(
                                video = video,
                                onVideoSelected = { video.localId?.let {onNavigateToAddNewVideoScreen(it) } }
                            )
                        }
                    }
                    StudioSelectedTag.PENDING_RE_UPLOAD -> {
                        items(uiState.draftVideos) { video ->
                            StudioVideoItem(
                                video = video,
                                onVideoSelected = {  }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class StudioSelectedTag {
    ACTIVE,
    PROCESSING,
    UPLOADING,
    DRAFT,
    PENDING_RE_UPLOAD,
}
