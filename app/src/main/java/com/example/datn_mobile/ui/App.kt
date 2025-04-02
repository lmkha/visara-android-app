package com.example.datn_mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datn_mobile.ui.components.MainContainer
import com.example.datn_mobile.ui.navigation.Destination
import com.example.datn_mobile.ui.screens.LoginScreen
import com.example.datn_mobile.ui.theme.DatnmobileTheme

@Composable
fun App() {
    DatnmobileTheme {
        val navController = rememberNavController()
        var authenticated by remember { mutableStateOf(false) }

        NavHost(
            navController = navController,
            startDestination = Destination.Main(),
        ) {
            composable<Destination.Main> {
                MainContainer(
                    navigateToLogin = { navController.navigate(Destination.Login) },
                    navigateToSearch = { navController.navigate(Destination.Search) },
                    authenticated = authenticated,
                )
            }
            composable<Destination.Search> { CheckScreen("Search Screen") }
            composable<Destination.Login> {
                LoginScreen {
                    authenticated = true
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun CheckScreen(name: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(name)
    }
}
