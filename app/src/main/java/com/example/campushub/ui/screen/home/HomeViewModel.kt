package com.example.campushub.ui.screen.home

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val postRepository: PostRepository = MockPostRepository(),
    private val userRepository: UserRepository = MockUserRepository.getInstance()
) : ViewModel() {

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadPosts()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            currentUser = userRepository.getCurrentUser().firstOrNull()
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            isLoading = true
            postRepository.getPosts().collectLatest {
                posts = it
                isLoading = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        viewModelScope.launch {
            postRepository.searchPosts(query).collectLatest {
                posts = it
            }
        }
    }

    fun toggleLike(postId: String) {
        posts = posts.map { post ->
            if (post.id == postId) {
                post.copy(
                    isLiked = !post.isLiked,
                    likes = if (post.isLiked) post.likes - 1 else post.likes + 1
                )
            } else post
        }
        viewModelScope.launch {
            postRepository.toggleLike(postId)
        }
    }

    fun toggleFavorite(postId: String) {
        posts = posts.map { post ->
            if (post.id == postId) {
                post.copy(isFavorite = !post.isFavorite)
            } else post
        }
        viewModelScope.launch {
            postRepository.toggleFavorite(postId)
        }
    }
}
