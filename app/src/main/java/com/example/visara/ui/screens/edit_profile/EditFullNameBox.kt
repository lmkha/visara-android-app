package com.example.visara.ui.screens.edit_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.visara.viewmodels.EditProfileScreenEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFullNameBox(
    modifier: Modifier = Modifier,
    currentFullName: String,
    onSave: (newFullName: String) -> Unit,
    onBack: () -> Unit,
    eventFlow: Flow<EditProfileScreenEvent>,
) {

    var newFullName by remember(currentFullName) { mutableStateOf(currentFullName) }
    var isProcessing by remember { mutableStateOf(false) }

    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifeCycleOwner.lifecycle) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            eventFlow.collect { event ->
                when (event) {
                    EditProfileScreenEvent.UpdateFullNameFailure -> {
                        isProcessing = false
                    }
                    EditProfileScreenEvent.UpdateFullNameSuccess -> {
                        isProcessing = false
                        onBack()
                    }
                    else -> {}
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Full name",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Button(
                        enabled = newFullName.isNotBlank() &&
                                newFullName != currentFullName &&
                                newFullName.length <= 30
                        ,
                        onClick = {
                            isProcessing = true
                            onSave(newFullName)
                        }
                    ) {
                        if (!isProcessing) {
                            Text(
                                text = "Save",
                                fontWeight = FontWeight.Bold,
                            )
                        } else {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        modifier = modifier,
    ) { scaffoldPadding->
        Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding).padding(horizontal = 8.dp)) {
            Text(
                text = "Name",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextField(
                value = newFullName,
                onValueChange = { newFullName = it },
                trailingIcon = {
                    Text(
                        text = "${newFullName.length}/30",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp))
            )
        }
    }
}
