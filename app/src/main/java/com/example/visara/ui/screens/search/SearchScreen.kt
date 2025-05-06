package com.example.visara.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.visara.R
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    goBack: () -> Unit,
) {
    val textFieldState = remember { TextFieldState() }
    var filteredResults by remember { mutableStateOf(emptyList<String>()) }
    val allResults = listOf("barca", "real", "milano", "liver", "bayer", "chelsea")
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val resultLazyListState = rememberLazyListState()

    LaunchedEffect(textFieldState.text) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(500)
            filteredResults = if (textFieldState.text.isBlank()) emptyList()
            else allResults.filter { it.contains(other = textFieldState.text, ignoreCase = true) }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(bottom = 8.dp),
                    title = {
                        SimpleSearchBar(
                            textFieldState = textFieldState,
                            onSearch = { newQuery ->
                                textFieldState.edit {
                                    replace(0, length, newQuery)
                                }
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "GoBack"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.mic_24px),
                                contentDescription = "Mic icon",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    expandedHeight = 90.dp,
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding,
                state = resultLazyListState,
            ) {
                items(count = filteredResults.size) { index ->
                    val resultText = filteredResults[index]
                    ListItem(
                        headlineContent = { Text(resultText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { textFieldState.edit { replace(0, length, resultText) } }
                    )
                }
//                items(10) {
//                    VideoItem(
//                        onVideoSelect = onSelectResult,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    )
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState.text.toString(),
                onQueryChange = { newText -> onSearch(newText) },
                onSearch = { onSearch(textFieldState.text.toString()) },
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text("Search ....") },
                colors = SearchBarDefaults.inputFieldColors(
                    cursorColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        expanded = false,
        onExpandedChange = {},
        content = {},
        colors = SearchBarDefaults.colors(
            containerColor = LocalVisaraCustomColors.current.darkGrayCustom,
            dividerColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}
