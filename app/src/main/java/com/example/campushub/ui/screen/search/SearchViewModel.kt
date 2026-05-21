package com.example.campushub.ui.screen.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campushub.data.model.Post
import com.example.campushub.data.repository.PostRepository
import com.example.campushub.data.repository.impl.MockPostRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val postRepository: PostRepository = MockPostRepository()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<Post>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
        if (newQuery.isBlank()) {
            searchResults = emptyList()
            return
        }

        viewModelScope.launch {
            isLoading = true
            postRepository.searchPosts(newQuery).collectLatest {
                searchResults = it
                isLoading = false
            }
        }
    }

    fun toggleLike(postId: String) {
        searchResults = searchResults.map { post ->
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
        searchResults = searchResults.map { post ->
            if (post.id == postId) {
                post.copy(isFavorite = !post.isFavorite)
            } else post
        }
        viewModelScope.launch {
            postRepository.toggleFavorite(postId)
        }
    }
}
