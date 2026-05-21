package com.example.campushub.data.repository.impl

import com.example.campushub.data.api.AddCommentRequest
import com.example.campushub.data.api.ApiService
import com.example.campushub.data.api.CreatePostRequest
import com.example.campushub.data.api.UpdatePostRequest
import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import com.example.campushub.data.repository.PostRepository
import com.example.campushub.utils.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class ApiPostRepository(private val api: ApiService) : PostRepository {

    private val _postsFlow = MutableStateFlow<List<Post>>(emptyList())

    private suspend fun <T> safeCall(block: suspend () -> Result<T>): Result<T> {
        if (!NetworkUtils.isNetworkAvailable()) {
            return Result.failure(IOException("网络不可用，请检查网络连接"))
        }
        return try {
            block()
        } catch (e: IOException) {
            Result.failure(IOException("网络请求失败，请稍后重试"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "服务异常，请稍后重试"))
        }
    }

    override fun getPosts(): Flow<List<Post>> = flow {
        while (true) {
            try {
                if (NetworkUtils.isNetworkAvailable()) {
                    val response = api.getPosts()
                    if (response.code == 0 && response.data != null) {
                        _postsFlow.value = response.data
                        emit(response.data)
                    }
                } else {
                    emit(_postsFlow.value)
                }
            } catch (_: IOException) {
                emit(_postsFlow.value)
            } catch (_: Exception) {
                emit(_postsFlow.value)
            }
            delay(10_000L)
        }
    }

    override fun getPostById(postId: String): Flow<Post?> = flow {
        if (!NetworkUtils.isNetworkAvailable()) {
            val cached = _postsFlow.value.find { it.id == postId }
            emit(cached)
            return@flow
        }
        try {
            val response = api.getPostById(postId)
            emit(if (response.code == 0) response.data else null)
        } catch (_: Exception) {
            val cached = _postsFlow.value.find { it.id == postId }
            emit(cached)
        }
    }

    override fun getComments(postId: String): Flow<List<Comment>> = flow {
        if (!NetworkUtils.isNetworkAvailable()) {
            emit(emptyList())
            return@flow
        }
        try {
            val response = api.getComments(postId)
            emit(
                if (response.code == 0 && response.data != null) response.data
                else emptyList()
            )
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    override fun getPostsByAuthor(author: String): Flow<List<Post>> =
        _postsFlow.asStateFlow().map { it.filter { post -> post.author == author } }

    override fun getFavoritePosts(): Flow<List<Post>> =
        _postsFlow.asStateFlow().map { it.filter { post -> post.isFavorite } }

    override fun searchPosts(query: String): Flow<List<Post>> = flow {
        if (!NetworkUtils.isNetworkAvailable()) {
            val results = _postsFlow.value.filter {
                it.title.contains(query, ignoreCase = true)
            }
            emit(results)
            return@flow
        }
        try {
            val response = api.searchPosts(query)
            emit(
                if (response.code == 0 && response.data != null) response.data
                else emptyList()
            )
        } catch (_: Exception) {
            val results = _postsFlow.value.filter {
                it.title.contains(query, ignoreCase = true)
            }
            emit(results)
        }
    }

    override suspend fun createPost(
        title: String,
        content: String,
        author: String,
        imageUrls: List<String>
    ): Result<Post> = safeCall {
        val response = api.createPost(
            CreatePostRequest(title, content, author, imageUrls)
        )
        if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "发布失败" }))
        }
    }

    override suspend fun updatePost(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>
    ): Result<Post> = safeCall {
        val response = api.updatePost(
            postId,
            UpdatePostRequest(title, content, imageUrls)
        )
        if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "更新失败" }))
        }
    }

    override suspend fun addComment(
        postId: String,
        content: String,
        author: String
    ): Result<Comment> = safeCall {
        val response = api.addComment(
            postId,
            AddCommentRequest(content, author)
        )
        if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "评论失败" }))
        }
    }

    override suspend fun addReply(
        commentId: String,
        content: String,
        author: String
    ): Result<Comment> = safeCall {
        val response = api.addReply(
            commentId,
            AddCommentRequest(content, author)
        )
        if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "回复失败" }))
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> = safeCall {
        val response = api.deletePost(postId)
        if (response.code == 0) Result.success(Unit)
        else Result.failure(Exception(response.message.ifBlank { "删除失败" }))
    }

    override suspend fun toggleLike(postId: String): Result<Unit> = safeCall {
        val response = api.toggleLike(postId)
        if (response.code == 0) Result.success(Unit)
        else Result.failure(Exception(response.message.ifBlank { "操作失败" }))
    }

    override suspend fun toggleFavorite(postId: String): Result<Unit> = safeCall {
        val response = api.toggleFavorite(postId)
        if (response.code == 0) Result.success(Unit)
        else Result.failure(Exception(response.message.ifBlank { "操作失败" }))
    }
}
