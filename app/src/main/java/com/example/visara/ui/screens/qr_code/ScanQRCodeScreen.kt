package com.example.visara.ui.screens.qr_code

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.ui.components.ObserverAsEvents
import com.example.visara.viewmodels.ScanBy
import com.example.visara.viewmodels.ScanQRCodeScreenEvent
import com.example.visara.viewmodels.ScanQRCodeViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRCodeScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanQRCodeViewModel = hiltViewModel(),
    onNavigateToProfileScreen: (username: String) -> Unit,
    onNavigateToUnrecognizedQRCodeDataScreen: (rawData: String) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserverAsEvents(viewModel.eventFlow) { event ->
        when (event) {
            is ScanQRCodeScreenEvent.UnrecognizedDataScanned -> {
                onNavigateToUnrecognizedQRCodeDataScreen(event.rawData)
            }
            is ScanQRCodeScreenEvent.ProfileScanned -> {
                onNavigateToProfileScreen(event.username)
            }
            is ScanQRCodeScreenEvent.ScanFailed -> {

            }
            is ScanQRCodeScreenEvent.VideoScanned -> {

            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.scanBy == ScanBy.CAMERA_DIRECTLY) {
                            onBack()
                        } else if (uiState.scanBy == ScanBy.IMAGE_FROM_DEVICE) {
                            viewModel.changeScanBy(ScanBy.CAMERA_DIRECTLY)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
    ) {
        when (uiState.scanBy) {
            ScanBy.CAMERA_DIRECTLY -> {
                ScanQRCodeByCameraScreen(
                    barcodeAnalyzer = viewModel.barcodeAnalyzer,
                    onChangeScanBy = { viewModel.changeScanBy(ScanBy.IMAGE_FROM_DEVICE) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            ScanBy.IMAGE_FROM_DEVICE -> {
                ScanQRCodeByImageUriScreen(
                    onScan = { viewModel.scanFromImageUri(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                )
            }
        }
    }
}
