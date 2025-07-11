package com.example.visara.ui.screens.qr_code

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar
import com.example.visara.viewmodels.MyQRCodeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyQRCodeScreen(
    modifier: Modifier = Modifier,
    viewModel: MyQRCodeScreenViewModel = hiltViewModel(),
    onNavigateToScanQRCodeScreen: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToScanQRCodeScreen) {
                            Icon(
                                painter = painterResource(R.drawable.qr_code_scanner_24px),
                                contentDescription = "QR Code scanner"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    )
                )
            },
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val boxSize = 600.dp
                val avatarSize = 100.dp
                val qrCodeSize = boxSize - avatarSize / 2
                Box(modifier = Modifier.size(boxSize)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(qrCodeSize)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .align(Alignment.BottomCenter)
                            .background(Color.White)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 70.dp)

                        ) {
                            Text(
                                text = uiState.username ?: "username",
                                fontWeight = FontWeight.Bold,
                            )

                            uiState.qrCodeBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR Code bitmap",
                                    modifier = Modifier.size(330.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.app_logo),
                                contentDescription = "App logo",
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = stringResource(R.string.app_name).drop(1),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(115.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.TopCenter)
                    ) {
                        UserAvatar(
                            avatarLink = uiState.userAvatarUrl,
                            modifier = Modifier
                                .size(avatarSize)
                        )
                    }
                }
            }
        }
    }
}
