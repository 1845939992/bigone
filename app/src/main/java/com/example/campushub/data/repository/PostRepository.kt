package com.example.campushub.data.repository

import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * 帖子模块数据仓库接口
 */
interface PostRepository {
    fun getPosts(): Flow<List<Post>>
    fun getPostById(postId: String): Flow<Post?>
    fun getComments(postId: String): Flow<List<Comment>>
    fun getPostsByAuthor(author: String): Flow<List<Post>>
    fun getFavoritePosts(): Flow<List<Post>>
    fun searchPosts(query: String): Flow<List<Post>>
    suspend fun createPost(title: String, content: String, author: String, imageUrls: List<String> = emptyList()): Result<Post>
    suspend fun updatePost(postId: String, title: String, content: String, imageUrls: List<String> = emptyList()): Result<Post>
    suspend fun addComment(postId: String, content: String, author: String): Result<Comment>
    suspend fun addReply(commentId: String, content: String, author: String): Result<Comment>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun toggleLike(postId: String): Result<Unit>
    suspend fun toggleFavorite(postId: String): Result<Unit>
}
