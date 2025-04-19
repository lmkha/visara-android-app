package com.example.visara.ui.screens.following

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.ui.theme.AppTheme

// New video of user that you are following
@Composable
fun FollowingScreen(
    modifier: Modifier = Modifier,
    onChangeTheme: (theme: AppTheme) -> Unit,
    bottomNavBar: @Composable () -> Unit,
    navigateToTestScreen: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { bottomNavBar() },
    ) { innerPadding->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { onChangeTheme(AppTheme.LIGHT) }) { Text("Light theme") }
            Button(onClick = { onChangeTheme(AppTheme.DARK) }) { Text("Dark theme") }
            Button(onClick = { onChangeTheme(AppTheme.SYSTEM) }) { Text("System theme") }
            Button(onClick = navigateToTestScreen) { Text("Test") }
        }
    }
}
