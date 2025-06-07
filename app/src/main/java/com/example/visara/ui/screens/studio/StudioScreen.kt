package com.example.visara.ui.screens.studio

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.VideoThumbnailFromVideoUri
import com.example.visara.ui.utils.toTimeAgo
import com.example.visara.viewmodels.StudioViewModel
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    modifier: Modifier = Modifier,
    viewModel: StudioViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            Column(
                modifier = Modifier
                    .padding(innerPaddingValue)
                    .padding(horizontal = 4.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Video is being uploaded") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
                if (uiState.postingVideo != null) {
                    uiState.postingVideo?.let { postingVideo ->
                        PostingVideoItem(
                            video = postingVideo,
                            onVideoSelected = {},
                            modifier = Modifier
                        )
                    }
                }
            }

            if (uiState.postingVideo == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "There is no video being posted",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}
@Composable
private fun PostingVideoItem(
    modifier: Modifier = Modifier,
    video: VideoModel,
    onVideoSelected: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .clickable { onVideoSelected() }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (video.localThumbnailUri != null) {
                    AsyncImage(
                        model = video.localThumbnailUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = MaterialTheme.colorScheme.surface)
                    )
                } else {
                    VideoThumbnailFromVideoUri(
                        uri = video.localVideoUri,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = MaterialTheme.colorScheme.surface)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = 8.dp,
                            end = 8.dp,
                        )
                        .width(70.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Black)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getVideoDurationFormatted(context, video.localVideoUri),
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.wrapContentWidth(),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = video.title,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = video.createdAt.toTimeAgo(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = if (!video.isUploaded)  Color.Cyan.copy(0.7f)
                            else if (!video.isProcessed) Color.Green.copy(0.3f)
                            else Color.Green
                        )
                ) {
                    Text(
                        text = if (!video.isUploaded) "Uploading"
                        else if (!video.isProcessed) "Processing"
                        else "Processed"
                        ,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}

private fun getVideoDurationFormatted(context: Context, videoUri: Uri?): String {
    if (videoUri == null) return "00:00:00"
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millis = durationString?.toLongOrNull() ?: 0L
        formatDuration(millis)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        "00:00:00"
    } catch (e: IOException) {
        e.printStackTrace()
        "00:00:00"
    } catch (e: SecurityException) {
        e.printStackTrace()
        "00:00:00"
    } finally {
        try {
            retriever.release()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
