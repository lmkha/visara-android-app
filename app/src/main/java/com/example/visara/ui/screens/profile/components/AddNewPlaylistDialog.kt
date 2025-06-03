package com.example.visara.ui.screens.profile.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AddNewPlaylistDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSubmit: (title: String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var privacy by remember { mutableStateOf("public") }
    val maxAllowedTitleLength = 10
    val titleErrorMessage by remember {
        derivedStateOf {
            if (title.length > maxAllowedTitleLength) {
                "Title must be at most $maxAllowedTitleLength characters"
            } else {
                ""
            }
        }
    }
    var isAdding by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = modifier
                .width(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "New playlist",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                TextField(
                    value = title,
                    label = { Text("Title") },
                    onValueChange = { title = it },
                    maxLines = 1,
                    trailingIcon = {
                        Text("${title.length}/$maxAllowedTitleLength")
                    },
                    isError = title.length > maxAllowedTitleLength,
                    supportingText = {
                        Text(
                            text = titleErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                )

                Column(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Privacy",
                        fontWeight = FontWeight.Medium,
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Public")
                            RadioButton(
                                selected = privacy == "public",
                                onClick = { privacy = "public" }
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Private")
                            RadioButton(
                                selected = privacy == "private",
                                onClick = { privacy = "private" }
                            )
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onDismissRequest
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                isAdding = true
                                onSubmit(title)
                            },
                            enabled = title.length <= maxAllowedTitleLength,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isAdding) MaterialTheme.colorScheme.primary
                                else Color.LightGray
                            ),
                        ) {
                            if (!isAdding) {
                                Text("Create")
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = Color.Black,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
