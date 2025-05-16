package com.example.visara.ui.screens.following_feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// New video of user that you are following
@Composable
fun FollowingFeedScreen(
    modifier: Modifier = Modifier,
    bottomNavBar: @Composable () -> Unit,
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

        }
    }
}
