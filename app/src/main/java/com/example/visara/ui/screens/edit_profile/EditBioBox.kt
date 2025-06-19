package com.example.visara.ui.screens.edit_profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
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
fun EditBioBox(
    modifier: Modifier = Modifier,
    currentBio: String,
    onSave: (newBio: String) -> Unit,
    onBack: () -> Unit,
    eventFlow: Flow<EditProfileScreenEvent>,
) {

    var newBio by remember(currentBio) { mutableStateOf(currentBio) }
    var isProcessing by remember { mutableStateOf(false) }

    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifeCycleOwner.lifecycle) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            eventFlow.collect { event ->
                when (event) {
                    EditProfileScreenEvent.UpdateBioFailure -> {
                        isProcessing = false
                    }
                    EditProfileScreenEvent.UpdateBioSuccess -> {
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
                        text = "Bio",
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
                        enabled = newBio != currentBio &&
                                newBio.length <= 50 &&
                                !isProcessing
                        ,
                        onClick = {
                            isProcessing = true
                            onSave(newBio)
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
                text = "Bio",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextField(
                value = newBio,
                placeholder = {
                    if (newBio.isBlank()) {
                        Text("Add bio ...")
                    }
                },
                onValueChange = { newBio = it },
                minLines = 5,
                maxLines = 7,
                trailingIcon = {
                    Box(modifier = Modifier.height(120.dp)) {
                        Text(
                            text = "${newBio.length}/50",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(horizontal = 4.dp)
                        )
                    }
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
