package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.SearchRepository
import com.example.visara.data.repository.VideoDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val videoDetailRepository: VideoDetailRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SearchScreenUiState> = MutableStateFlow(SearchScreenUiState())
    val uiState: StateFlow<SearchScreenUiState> = _uiState.asStateFlow()

    fun searchVideoByTitle(pattern: String) {
        viewModelScope.launch {
            uiState.value.let { currentState ->
                if (pattern != currentState.videoPattern || currentState.searchType != SearchType.TITLE) {
                    val videos = searchRepository.searchVideo("title", pattern)
                    _uiState.update {
                        it.copy(
                            videos = videos,
                            videoPattern = pattern,
                            searchType = SearchType.TITLE,
                            hasSearched = true,
                            pattern = pattern,
                        )
                    }
                }
            }
        }
    }

    fun searchVideoByHashtag(pattern: String) {
        viewModelScope.launch {
            uiState.value.let { currentState ->
                if (pattern != currentState.videoPattern || currentState.searchType != SearchType.HASHTAG) {
                    val videos = searchRepository.searchVideo("hashtag", pattern)
                    _uiState.update {
                        it.copy(
                            videos = videos,
                            videoPattern = pattern,
                            searchType = SearchType.HASHTAG,
                            hasSearched = true,
                            pattern = pattern,
                        )
                    }
                }
            }
        }
    }

    fun searchUser(pattern: String) {
        viewModelScope.launch {
            if (pattern != uiState.value.userPattern) {
                val users = searchRepository.searchUser(pattern)
                _uiState.update {
                    it.copy(
                        users = users,
                        userPattern = pattern,
                        searchType = SearchType.USER,
                        hasSearched = true,
                        pattern = pattern,
                    )
                }
            }
        }
    }

    fun selectVideo(videoModel: VideoModel) {
        viewModelScope.launch {
            videoDetailRepository.setVideoDetail(videoModel)
        }
    }
}

data class SearchScreenUiState(
    val videos: List<VideoModel> = emptyList(),
    val users: List<UserModel> = emptyList(),
    val userPattern: String = "",
    val videoPattern: String = "",
    val pattern: String = "",
    val searchType: SearchType = SearchType.TITLE,
    val hasSearched: Boolean = false,
)

enum class SearchType {
    TITLE,
    HASHTAG,
    USER,
}
