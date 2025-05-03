package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.CommentModel
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.CommentRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
) : ViewModel() {
    private val _uiState: MutableStateFlow<VideoDetailScreenUiState> = MutableStateFlow(
        VideoDetailScreenUiState(
            isUserAuthenticated = authRepository.isAuthenticated.value,
        )
    )
    val uiState = _uiState.asStateFlow()
    var likeCommentJobMap = mutableMapOf<String, Job>()

    init {
        observerAuthenticationState()
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()
            if (currentUser != null) {
                _uiState.update { it.copy(currentUser = currentUser) }
            }
        }
    }

    fun setVideo(videoModel: VideoModel) {
        viewModelScope.launch {
            val video = videoRepository.getVideoById(videoModel.id)
            if (video != null) {
                _uiState.update {
                    VideoDetailScreenUiState(
                        video = video,
                        isUserAuthenticated = authRepository.isAuthenticated.value,
                    )
                }
            } else {
                _uiState.update {
                    VideoDetailScreenUiState(
                        video = videoModel,
                        isUserAuthenticated = authRepository.isAuthenticated.value,
                    )
                }
            }
            _uiState.value.video.id.let { videoId->
                if (videoId.isNotBlank()) {
                    val parentComments = commentRepository.getAllParentCommentsByVideoId(videoId)
                    val commentList = parentComments.map { CommentWithReplies(comment = it)}
                    _uiState.update { it.copy(commentList = commentList) }
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
    fun changeCommentLike(commentId: String, current: Boolean = false, onFailure: () -> Unit) {
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
        repliedCommentIndex: Int?,
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
                val newCommentCount = oldState.video.commentsCount + 1
                if (repliedCommentId == null) {
                    val updatedList = listOf(CommentWithReplies(comment = newDraftComment)) + oldState.commentList
                    oldState.copy(
                        commentList = updatedList,
                        video = oldState.video.copy(commentsCount = newCommentCount)
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
                        video = oldState.video.copy(commentsCount = newCommentCount)
                    )
                }
            }

            val newComment = commentRepository.addComment(
                videoId = _uiState.value.video.id,
                replyTo = repliedCommentId,
                content = content,
            )

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
                    val newCommentCount = oldState.video.commentsCount - 1
                    if (repliedCommentId == null) {
                        val updatedList = oldState.commentList.filterNot { it.comment?.id == newDraftComment.id }
                        oldState.copy(
                            commentList = updatedList,
                            video = oldState.video.copy(commentsCount = newCommentCount)
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
                            video = oldState.video.copy(commentsCount = newCommentCount),
                        )
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
}

data class VideoDetailScreenUiState(
    val isUserAuthenticated: Boolean = false,
    val video: VideoModel = VideoModel(),
    val commentList: List<CommentWithReplies> = emptyList(),
    val isLoading: Boolean = true,
    val isOpenExpandedCommentSection: Boolean = false,
    val currentUser: UserModel? = null,
    val isAddingComment: Boolean = false,
)

data class CommentWithReplies(
    val comment: CommentModel? = null,
    val replies: List<CommentModel> = emptyList(),
)
