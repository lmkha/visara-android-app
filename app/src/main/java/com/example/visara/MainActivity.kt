package com.example.visara

import android.Manifest
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.visara.ui.App
import com.example.visara.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.content.res.Configuration
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.visara.service.play_back.PlaybackService
import com.google.common.util.concurrent.MoreExecutors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()

        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            controllerFuture.addListener({
                appViewModel.setPlayer(controllerFuture.get())
            }, MoreExecutors.directExecutor())

            val orientation = LocalConfiguration.current.orientation
            LaunchedEffect(orientation) {
                when(orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                    }
                    else -> {
                        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                    }
                }
            }

            App(
                appViewModel = appViewModel,
                requireLandscapeMode = {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                },
                requirePortraitMode = {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                },
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        /*
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
         */
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
