package com.example.visara.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.login.LoginScreen
import com.example.visara.ui.theme.VisaraTheme
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App() {
    VisaraTheme(dynamicColor = false) {
        val navController = rememberNavController()
        var authenticated by remember { mutableStateOf(false) }
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        var snackBarBottomPadding by remember { mutableStateOf(0.dp) }

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier
                        .padding(bottom = snackBarBottomPadding)
                )
            }
        ) {
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = Destination.Main(),
            ) {
                composable<Destination.Main> {
                    MainContainer(
                        navigateToLogin = { navController.navigate(Destination.Login) },
                        authenticated = authenticated,
                        displaySnackBar = { message, bottomPadding->
                            coroutineScope.launch {
                                snackBarBottomPadding = bottomPadding

                                val result = snackBarHostState
                                    .showSnackbar(
                                        message = message,
                                        actionLabel = "Try again",
                                        duration = SnackbarDuration.Long,
                                    )

                                when (result) {
                                    SnackbarResult.Dismissed -> {

                                    }
                                    SnackbarResult.ActionPerformed -> {

                                    }
                                }
                            }
                        }
                    )
                }
                composable<Destination.Login> {
                    LoginScreen {
                        authenticated = true
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
