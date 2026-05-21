package com.example.campushub.ui.screen.post

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campushub.data.repository.PostRepository
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.data.repository.impl.MockPostRepository
import com.example.campushub.data.repository.impl.MockUserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository = MockPostRepository(),
    private val userRepository: UserRepository = MockUserRepository.getInstance()
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var content by mutableStateOf("")
        private set

    var selectedImages by mutableStateOf<List<Uri>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _eventFlow = MutableSharedFlow<PostEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val maxImageCount = 9

    fun updateTitle(value: String) {
        title = value
    }

    fun updateContent(value: String) {
        content = value
    }

    fun addImages(uris: List<Uri>) {
        val remaining = maxImageCount - selectedImages.size
        if (remaining <= 0) return
        val toAdd = uris.take(remaining)
        selectedImages = selectedImages + toAdd
    }

    fun removeImage(index: Int) {
        selectedImages = selectedImages.toMutableList().also { it.removeAt(index) }
    }

    fun onPostClick() {
        if (title.isBlank() || content.isBlank()) {
            sendError("标题和内容不能为空")
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val author = userRepository.getCurrentUser().firstOrNull()?.nickname ?: "匿名用户"
                val imageUrlStrings = selectedImages.map { it.toString() }
                val result = postRepository.createPost(
                    title = title,
                    content = content,
                    author = author,
                    imageUrls = imageUrlStrings
                )

                result.fold(
                    onSuccess = { _eventFlow.emit(PostEvent.PostSuccess) },
                    onFailure = { _eventFlow.emit(PostEvent.ShowError(it.message ?: "发布失败")) }
                )
            } catch (e: Exception) {
                _eventFlow.emit(PostEvent.ShowError(e.message ?: "发布失败"))
            } finally {
                isLoading = false
            }
        }
    }

    private fun sendError(msg: String) {
        viewModelScope.launch { _eventFlow.emit(PostEvent.ShowError(msg)) }
    }

    sealed class PostEvent {
        object PostSuccess : PostEvent()
        data class ShowError(val message: String) : PostEvent()
    }
}
