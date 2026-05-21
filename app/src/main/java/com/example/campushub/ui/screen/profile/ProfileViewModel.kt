package com.example.campushub.ui.screen.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campushub.data.model.Post
import com.example.campushub.data.model.User
import com.example.campushub.data.repository.PostRepository
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.data.repository.impl.MockPostRepository
import com.example.campushub.data.repository.impl.MockUserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = MockUserRepository.getInstance(),
    private val postRepository: PostRepository = MockPostRepository()
) : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var myPosts by mutableStateOf<List<Post>>(emptyList())
        private set

    var myFavorites by mutableStateOf<List<Post>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            isLoading = true
            try {
                currentUser = userRepository.getCurrentUser().firstOrNull()
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }

        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { user ->
                val authorName = user?.nickname ?: ""
                if (authorName.isNotBlank()) {
                    postRepository.getPostsByAuthor(authorName)
                        .catch { snackbarMessage = "加载帖子失败" }
                        .collectLatest { myPosts = it }
                } else {
                    myPosts = emptyList()
                }
            }
        }

        viewModelScope.launch {
            postRepository.getFavoritePosts()
                .catch { }
                .collectLatest { myFavorites = it }
        }
    }

    fun updateProfile(nickname: String, signature: String, school: String, avatarUrl: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                userRepository.updateProfile(nickname, signature, school, avatarUrl)
                    .onSuccess { snackbarMessage = "个人资料更新成功" }
                    .onFailure { e -> snackbarMessage = e.message ?: "更新失败" }
            } catch (e: Exception) {
                snackbarMessage = "操作失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                userRepository.changePassword(oldPassword, newPassword)
                    .onSuccess { snackbarMessage = "密码修改成功" }
                    .onFailure { e -> snackbarMessage = e.message ?: "修改失败" }
            } catch (e: Exception) {
                snackbarMessage = "操作失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                postRepository.deletePost(postId)
                    .onSuccess { snackbarMessage = "帖子已删除" }
                    .onFailure { e -> snackbarMessage = e.message ?: "删除失败" }
            } catch (e: Exception) {
                snackbarMessage = "操作失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun editPost(postId: String, title: String, content: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                postRepository.updatePost(postId, title, content)
                    .onSuccess { snackbarMessage = "帖子已更新" }
                    .onFailure { e -> snackbarMessage = e.message ?: "编辑失败" }
            } catch (e: Exception) {
                snackbarMessage = "操作失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearSnackbar() {
        snackbarMessage = null
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        userRepository.logout()
        onLogoutSuccess()
    }
}
