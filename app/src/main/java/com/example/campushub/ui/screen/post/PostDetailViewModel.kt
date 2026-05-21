package com.example.campushub.ui.screen.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import com.example.campushub.data.repository.PostRepository
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.data.repository.impl.MockPostRepository
import com.example.campushub.data.repository.impl.MockUserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postId: String,
    private val postRepository: PostRepository = MockPostRepository(),
    private val userRepository: UserRepository = MockUserRepository.getInstance()
) : ViewModel() {

    var post by mutableStateOf<Post?>(null)
        private set

    var comments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            isLoading = true
            try {
                post = postRepository.getPostById(postId).firstOrNull()
                comments = postRepository.getComments(postId).firstOrNull().orEmpty()
            } catch (e: Exception) {
                snackbarMessage = "加载失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun refreshData() {
        loadData()
    }

    fun toggleLike() {
        post = post?.let { p ->
            p.copy(isLiked = !p.isLiked, likes = if (p.isLiked) p.likes - 1 else p.likes + 1)
        }
        viewModelScope.launch {
            postRepository.toggleLike(postId)
                .onFailure { snackbarMessage = "操作失败" }
        }
    }

    fun toggleFavorite() {
        val newFavorite = post?.isFavorite != true
        post = post?.copy(isFavorite = newFavorite)
        snackbarMessage = if (newFavorite) "已收藏" else "已取消收藏"
        viewModelScope.launch {
            postRepository.toggleFavorite(postId)
                .onFailure { snackbarMessage = "操作失败" }
        }
    }

    fun addComment(content: String) {
        viewModelScope.launch {
            try {
                val author = userRepository.getCurrentUser().firstOrNull()?.nickname ?: "匿名用户"
                postRepository.addComment(postId, content, author)
                    .onSuccess { newComment ->
                        snackbarMessage = "评论成功"
                        comments = listOf(newComment) + comments
                        try {
                            comments = postRepository.getComments(postId)
                                .firstOrNull()?.toList().orEmpty()
                        } catch (_: Exception) {
                        }
                    }
                    .onFailure { e ->
                        snackbarMessage = e.message ?: "评论失败"
                    }
            } catch (e: Exception) {
                snackbarMessage = "操作失败: ${e.message}"
            }
        }
    }

    fun addReply(commentId: String, content: String) {
        viewModelScope.launch {
            try {
                val author = userRepository.getCurrentUser().firstOrNull()?.nickname ?: "匿名用户"
                postRepository.addReply(commentId, content, author)
                    .onSuccess { reply ->
                        snackbarMessage = "回复成功"
                        comments = comments.map { comment ->
                            if (comment.id == commentId) {
                                comment.copy(replies = listOf(reply) + comment.replies)
                            } else {
                                comment
                            }
                        }
                        try {
                            comments = postRepository.getComments(postId)
                                .firstOrNull()?.toList().orEmpty()
                        } catch (_: Exception) {
                        }
                    }
                    .onFailure { e ->
                        snackbarMessage = e.message ?: "回复失败"
                    }
            } catch (e: Exception) {
                snackbarMessage = "操作失败: ${e.message}"
            }
        }
    }

    fun clearSnackbar() {
        snackbarMessage = null
    }
}
