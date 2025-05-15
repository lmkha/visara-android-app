package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.visara.data.model.CommentModel
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.CommentRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository,
    private val videoDetailRepository: VideoDetailRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<VideoDetailScreenUiState> = MutableStateFlow(VideoDetailScreenUiState())
    val uiState: StateFlow<VideoDetailScreenUiState> = _uiState.asStateFlow()
    var likeCommentJobMap: MutableMap<String, Job> = mutableMapOf<String, Job>()
    private var changeVideoLikeJob: Job? = null
    private var followAuthorJob: Job? = null
    private var unfollowAuthorJob: Job? = null
    val player: ExoPlayer get() = videoDetailRepository.dashVideoPlayerManager.player

    init {
        observerAuthenticationState()
        observerVideoDetail()
        observerCurrentUser()
        observerRecentFollowingUsername()
        observerRecentUnfollowUsername()
    }

    private fun observerVideoDetail() {
        viewModelScope.launch {
            videoDetailRepository.videoDetail.collect { videoDetail ->
                _uiState.update { oldState->
                    if (videoDetail.video?.id == oldState.video?.id) {
                        oldState.copy(
                            isFullScreenMode = videoDetail.isFullScreenMode,
                            isPlaying = videoDetail.isPlaying,
                        )
                    } else {
                        val videoId = videoDetail.video?.id
                        if (videoId != null) {
                            val videoDeferred = async { videoRepository.getVideoById(videoId) }
                            val isVideoLikedDeferred =  async { videoRepository.isVideoLiked(videoId) }
                            val authorDeferred = async { userRepository.getPublicUser(videoDetail.video.username) }
                            val isFollowingDeferred = async {
                                userRepository.checkIsFollowingThisUser(videoDetail.video.username)
                            }
                            val commentListDeferred = async {
                                val parentComments = commentRepository.getAllParentCommentsByVideoId(videoId)
                                parentComments.map { CommentWithReplies(comment = it) }
                            }

                            val video = videoDeferred.await()
                            val isVideoLiked = isVideoLikedDeferred.await()
                            val author = authorDeferred.await()
                            val isFollowing = isFollowingDeferred.await()
                            val commentList = commentListDeferred.await()

                            oldState.copy(
                                video = video,
                                isVideoLiked = isVideoLiked,
                                commentList = commentList,
                                author = author,
                                isFollowing = isFollowing,
                                isFullScreenMode = videoDetail.isFullScreenMode,
                                isPlaying = videoDetail.isPlaying,
                            )
                        } else {
                            VideoDetailScreenUiState(
                                isFullScreenMode = videoDetail.isFullScreenMode,
                                isPlaying = videoDetail.isPlaying,
                                isUserAuthenticated = oldState.isUserAuthenticated,
                                currentUser = oldState.currentUser,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observerAuthenticationState() {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isLogged->
                _uiState.update { it.copy(isUserAuthenticated = isLogged) }
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

    private fun observerRecentFollowingUsername() {
        viewModelScope.launch {
            userRepository.recentFollowingUsername.collect { currentFollowingUsername ->
                if (currentFollowingUsername != null && currentFollowingUsername == uiState.value.author?.username) {
                    _uiState.update { it.copy(isFollowing = true) }
                }
            }
        }
    }

    private fun observerRecentUnfollowUsername() {
        viewModelScope.launch {
            userRepository.recentUnfollowUsername.collect { currentUnfollowUsername ->
                if (currentUnfollowUsername != null && currentUnfollowUsername == uiState.value.author?.username) {
                    _uiState.update { it.copy(isFollowing = false) }
                }
            }
        }
    }

    fun fetchChildrenComment(parentCommentIndex: Int) {
        viewModelScope.launch {
            val parentComment = _uiState.value.commentList.getOrNull(parentCommentIndex) ?: return@launch
            val parentCommentId = parentComment.comment?.id ?: return@launch

            val childrenCommentModelList = commentRepository
                .getAllChildrenCommentByParentId(parentCommentId = parentCommentId)

            _uiState.update { oldState ->
                val updatedList = oldState.commentList.mapIndexed { index, item ->
                    if (index == parentCommentIndex) {
                        item.copy(replies = childrenCommentModelList)
                    } else {
                        item
                    }
                }
                oldState.copy(commentList = updatedList)
            }
        }
    }

    fun minimizeCommentSection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOpenExpandedCommentSection = false) }
        }
    }

    fun expandCommentSection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOpenExpandedCommentSection = true) }
        }
    }

    fun changeCommentLike(
        commentId: String,
        current: Boolean = false,
        onFailure: () -> Unit
    ) {
        likeCommentJobMap[commentId]?.cancel()

        val job = viewModelScope.launch {
            delay(500)
            var result: Boolean = if (current == true) {
                commentRepository.unlikeComment(commentId)
            } else {
                commentRepository.likeComment(commentId)
            }
            likeCommentJobMap.remove(commentId)
            if (result == false) { onFailure() }
        }

        likeCommentJobMap[commentId] = job
    }

    fun addComment(
        content: String,
        repliedCommentId: String?,
        repliedCommentIndex: Int?
    ) {
        viewModelScope.launch {
            val newDraftComment = CommentModel(
                id = "draft_${System.currentTimeMillis()}",
                content = content,
                username = _uiState.value.currentUser?.username ?: "username",
                isAdding = true,
            )

            // Add draft comment to commentList
            _uiState.update { oldState ->
                val newCommentCount = (oldState.video?.commentsCount ?: 0) + 1
                if (repliedCommentId == null) {
                    val updatedList = listOf(CommentWithReplies(comment = newDraftComment)) + oldState.commentList
                    oldState.copy(
                        commentList = updatedList,
                        video = oldState.video?.copy(commentsCount = newCommentCount)
                    )
                } else {
                    val updatedList = oldState.commentList.mapIndexed { index, item ->
                        if (index == repliedCommentIndex) {
                            val newReplyCount = (item.comment?.replyCount ?: 0) + 1
                            val updatedReplies = item.replies + newDraftComment
                            item.copy(
                                comment = item.comment?.copy(replyCount = newReplyCount),
                                replies = updatedReplies
                            )
                        } else item
                    }
                    oldState.copy(
                        commentList = updatedList,
                        video = oldState.video?.copy(commentsCount = newCommentCount)
                    )
                }
            }

            val newComment = _uiState.value.video?.id?.let {
                commentRepository.addComment(
                    videoId = it,
                    replyTo = repliedCommentId,
                    content = content,
                )
            }

            if (newComment != null) {
                // Replace draft with actual comment when add success
                _uiState.update { oldState ->
                    if (repliedCommentId == null) {
                        val updatedList = oldState.commentList.map {
                            if (it.comment?.id == newDraftComment.id) {
                                CommentWithReplies(comment = newComment)
                            } else it
                        }
                        oldState.copy(commentList = updatedList)
                    } else {
                        val updatedList = oldState.commentList.mapIndexed { index, item ->
                            if (index == repliedCommentIndex) {
                                val updatedReplies = item.replies.map {
                                    if (it.id == newDraftComment.id) newComment else it
                                }
                                item.copy(replies = updatedReplies)
                            } else item
                        }
                        oldState.copy(commentList = updatedList)
                    }
                }
            } else {
                // Rollback comment if failed
                _uiState.update { oldState ->
                    val newCommentCount = (oldState.video?.commentsCount ?: 0) - 1
                    if (repliedCommentId == null) {
                        val updatedList = oldState.commentList.filterNot { it.comment?.id == newDraftComment.id }
                        oldState.copy(
                            commentList = updatedList,
                            video = oldState.video?.copy(commentsCount = newCommentCount)
                        )
                    } else {
                        val updatedList = oldState.commentList.mapIndexed { index, item ->
                            if (index == repliedCommentIndex) {
                                val newReplyCount = (item.comment?.replyCount ?: 1) - 1
                                val updatedReplies = item.replies.filterNot { it.id == newDraftComment.id }
                                item.copy(
                                    comment = item.comment?.copy(replyCount = newReplyCount),
                                    replies = updatedReplies
                                )
                            } else item
                        }
                        oldState.copy(
                            commentList = updatedList,
                            video = oldState.video?.copy(commentsCount = newCommentCount),
                        )
                    }
                }
            }
        }
    }

    fun play() {
        viewModelScope.launch {
            videoDetailRepository.dashVideoPlayerManager.player.play()
        }
    }

    fun pause() {
        viewModelScope.launch {
            videoDetailRepository.dashVideoPlayerManager.player.pause()
        }
    }

    fun close() {
        viewModelScope.launch {
            videoDetailRepository.dashVideoPlayerManager.player.stop()
            videoDetailRepository.close()
        }
    }

    fun enableFullScreenMode() {
        viewModelScope.launch {
            videoDetailRepository.enableFullScreenMode()
        }
    }

    fun enableMinimizedMode() {
        viewModelScope.launch {
            videoDetailRepository.enableMinimizedMode()
        }
    }

    fun changeVideoLike(
        current: Boolean,
        onFailure: () -> Unit,
    ) {
        changeVideoLikeJob?.cancel()

        changeVideoLikeJob = viewModelScope.launch {
            delay(500)
            val videoId = _uiState.value.video?.id
            if (videoId != null) {
                val result = if (current == true) {
                    videoRepository.unlikeVideo(videoId)
                } else {
                    videoRepository.likeVideo(videoId)
                }
                if (!result) onFailure()
            }
            changeVideoLikeJob = null
        }
    }

    fun followAuthor(onFailure: () -> Unit) {
        followAuthorJob?.cancel()

        followAuthorJob = viewModelScope.launch {
            val authorUsername = uiState.value.author?.username
            if (authorUsername != null && !uiState.value.isFollowing) {
                val result = userRepository.followUser(authorUsername)
                if (!result) onFailure()
            }
            else onFailure()
            followAuthorJob = null
        }
    }

    fun unfollowAuthor(onFailure: () -> Unit) {
        unfollowAuthorJob?.cancel()

        unfollowAuthorJob = viewModelScope.launch {
            val authorUsername = uiState.value.author?.username
            if (authorUsername != null && uiState.value.isFollowing) {
                val result = userRepository.unfollowUser(authorUsername)
                if (!result) onFailure()
            }
            else onFailure()
            unfollowAuthorJob = null
        }
    }
}

data class VideoDetailScreenUiState(
    val isUserAuthenticated: Boolean = false,
    val video: VideoModel? = null,
    val isVideoLiked: Boolean = false,
    val commentList: List<CommentWithReplies> = emptyList(),
    val isLoading: Boolean = true,
    val isOpenExpandedCommentSection: Boolean = false,
    val currentUser: UserModel? = null,
    val author: UserModel? = null,
    val isFollowing: Boolean = false,
    val isAddingComment: Boolean = false,
    val isFullScreenMode: Boolean = false,
    val isPlaying: Boolean = false,
)

data class CommentWithReplies(
    val comment: CommentModel? = null,
    val replies: List<CommentModel> = emptyList(),
)
