package com.example.visara.ui.screens.inbox.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.visara.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInboxScreen(
    modifier: Modifier = Modifier,
    inboxId: String,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        UserAvatar(
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Column {
                            Text(
                                text = "Minh Kha",
                            )
                            Text(
                                text = "Hoạt động 8 phút trước",
                            )
                        }
                    }
                },
                navigationIcon = {

                },
                actions = {

                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }
    }
}
