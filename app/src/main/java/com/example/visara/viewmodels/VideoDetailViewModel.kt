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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val player: ExoPlayer get() = videoDetailRepository.dashVideoPlayerManager.player

    init {
        observerAuthenticationState()
        observerVideoDetail()
        observerCurrentUser()
    }

    private fun observerVideoDetail() {
        viewModelScope.launch {
            videoDetailRepository.videoDetail.collect { videoDetail ->
                val isUserAuthenticated = authRepository.isAuthenticated.first()

                val currentUser = userRepository.currentUser.first()

                val video = if (videoDetail.video?.id == _uiState.value.video?.id) _uiState.value.video
                else videoDetail.video?.id?.let { videoRepository.getVideoById(it) }

                val author = if (videoDetail.video?.username == _uiState.value.author?.username) _uiState.value.author
                else videoDetail.video?.username?.let { userRepository.getPublicUser(it) }

                val commentList = if (videoDetail.video?.id == null) emptyList()
                else if (videoDetail.video.id == _uiState.value.video?.id) _uiState.value.commentList
                else {
                    val parentComments = commentRepository.getAllParentCommentsByVideoId(videoDetail.video.id)
                    parentComments.map { CommentWithReplies(comment = it)}
                }

                _uiState.update {
                    VideoDetailScreenUiState(
                        isUserAuthenticated = isUserAuthenticated,
                        video = video,
                        commentList = commentList,
                        currentUser = currentUser,
                        author = author,
                        isFullScreenMode = videoDetail.isFullScreenMode,
                        isPlaying = videoDetail.isPlaying,
                    )
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
            videoDetailRepository.dashVideoPlayerManager.pause()
            videoDetailRepository.close()
        }
    }

    fun enableFullScreenMode() {
        viewModelScope.launch {
            videoDetailRepository.enableFullScreenMode()
        }
    }
}

data class VideoDetailScreenUiState(
    val isUserAuthenticated: Boolean = false,
    val video: VideoModel? = null,
    val commentList: List<CommentWithReplies> = emptyList(),
    val isLoading: Boolean = true,
    val isOpenExpandedCommentSection: Boolean = false,
    val currentUser: UserModel? = null,
    val author: UserModel? = null,
    val isAddingComment: Boolean = false,
    val isFullScreenMode: Boolean = false,
    val isPlaying: Boolean = false,
)

data class CommentWithReplies(
    val comment: CommentModel? = null,
    val replies: List<CommentModel> = emptyList(),
)
