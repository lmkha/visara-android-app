package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.FollowUserModel
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FollowScreenUiState> = MutableStateFlow(FollowScreenUiState())
    val uiState: StateFlow<FollowScreenUiState> = _uiState.asStateFlow()
    private var followJobMap: MutableMap<String, Job> = mutableMapOf<String, Job>()
    private var filterFollowingsJob: Job? = null
    private var filterFollowersJob: Job? = null

    init {
        observerAuthenticationState()
        observerCurrentUser()
    }

    private fun observerAuthenticationState() {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isAuthenticated->
                if (!isAuthenticated) {
                    _uiState.update { FollowScreenUiState() }
                }
            }
        }
    }

    private fun observerCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { currentUser ->
                _uiState.update { it.copy(currentUser = currentUser) }
            }
        }
    }

    fun setStartedTabIndex(index: Int) {
        _uiState.update { it.copy(startedTabIndex = index) }
    }

    fun fetchData() {
        fetchAllFollowings()
        fetchAllFollowers()
    }

    fun fetchAllFollowings() {
        if (!uiState.value.hadFetchedFollowings) {
            viewModelScope.launch {
                val followings = userRepository.getAllFollowings(0, 100)
                _uiState.update {
                    it.copy(
                        followings = followings,
                        hadFetchedFollowings = true
                    )
                }
            }
        }
    }

    fun fetchAllFollowers() {
        if (!uiState.value.hadFetchedFollowers) {
            viewModelScope.launch {
                val followers = userRepository.getAllFollowers(0, 100)
                _uiState.update {
                    it.copy(
                        followers = followers,
                        hadFetchedFollowers = true
                    )
                }
            }
        }
    }

    fun follow(user: FollowUserModel, onFailure: () -> Unit) {
        followJobMap[user.user.username]?.cancel()

        val job = viewModelScope.launch {
            delay(300)
            val result = userRepository.followUser(user.user.username)
            if (result) {
                var hadExistInFollowings = false
                var newFollowings = uiState.value.followings.map {
                    if (it.user.username == user.user.username) {
                        hadExistInFollowings = true
                        it.copy(isFollowing = true)
                    } else {
                        it
                    }
                }
                if (!hadExistInFollowings) newFollowings = newFollowings.plus(
                    user.copy(isFollowing = true)
                )

                val newFollowers = uiState.value.followers.map {
                    if (it.user.username == user.user.username) {
                        it.copy(isFollowing = true)
                    } else {
                        it
                    }
                }
                _uiState.update { it.copy(followings = newFollowings, followers = newFollowers) }
            } else {
                onFailure()
            }
            followJobMap.remove(user.user.username)
        }

        followJobMap[user.user.username] = job
    }

    fun unfollow(user: FollowUserModel, onFailure: () -> Unit) {
        followJobMap[user.user.username]?.cancel()

        val job = viewModelScope.launch {
            delay(300)
            val result = userRepository.unfollowUser(user.user.username)
            if (result) {
                val newFollowings = uiState.value.followings.map {
                    if (it.user.username == user.user.username) {
                        it.copy(isFollowing = false)
                    } else {
                        it
                    }
                }
                val newFollowers = uiState.value.followers.map {
                    if (it.user.username == user.user.username) {
                        it.copy(isFollowing = false)
                    } else {
                        it
                    }
                }
                _uiState.update { it.copy(followings = newFollowings, followers = newFollowers) }
            } else {
                onFailure()
            }
            followJobMap.remove(user.user.username)
        }

        followJobMap[user.user.username] = job
    }

    fun filterFollowings(pattern: String) {
        filterFollowingsJob?.cancel()

        filterFollowingsJob = viewModelScope.launch {
            delay(500)
            val trimmedPattern: String = pattern.trim()
            if (trimmedPattern.isBlank()) return@launch
            if (trimmedPattern != uiState.value.currentFollowingsFilterPattern) {
                val filteredFollowings: List<FollowUserModel> = _uiState.value.followings.filter {
                    it.user.username.contains(trimmedPattern) || it.user.fullName.contains(trimmedPattern)
                }
                _uiState.update {
                    it.copy(
                        filteredFollowings = filteredFollowings,
                        currentFollowingsFilterPattern = trimmedPattern,
                    )
                }
            }
        }
    }

    fun filterFollowers(pattern: String) {
        filterFollowersJob?.cancel()

        filterFollowersJob = viewModelScope.launch {
            delay(500)
            val trimmedPattern: String = pattern.trim()
            if (trimmedPattern.isBlank()) return@launch
            if (trimmedPattern != uiState.value.currentFollowersFilterPattern) {
                val filteredFollowers: List<FollowUserModel> = _uiState.value.followings.filter {
                    it.user.username.contains(trimmedPattern) || it.user.fullName.contains(trimmedPattern)
                }
                _uiState.update {
                    it.copy(
                        filteredFollowers = filteredFollowers,
                        currentFollowersFilterPattern = trimmedPattern,
                    )
                }
            }
        }
    }
}

data class FollowScreenUiState(
    val followers: List<FollowUserModel> = emptyList(),
    val filteredFollowers: List<FollowUserModel> = emptyList(),
    val followings: List<FollowUserModel> = emptyList(),
    val filteredFollowings: List<FollowUserModel> = emptyList(),
    val currentFollowingsFilterPattern: String = "",
    val currentFollowersFilterPattern: String = "",
    val hadFetchedFollowers: Boolean = false,
    val hadFetchedFollowings: Boolean = false,
    val startedTabIndex: Int = 0,
    val currentUser: UserModel? = null,
    val isLoading: Boolean = false,
)
