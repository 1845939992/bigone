package com.example.campushub.data.repository.impl

import com.example.campushub.data.MockData
import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import com.example.campushub.data.repository.PostRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MockPostRepository : PostRepository {

    override fun getPosts(): Flow<List<Post>> = MockData.postsFlow

    override fun getPostById(postId: String): Flow<Post?> = flow {
        delay(400)
        emit(MockData.posts.find { it.id == postId })
    }

    override fun getComments(postId: String): Flow<List<Comment>> = flow {
        delay(600)
        emit(MockData.getComments(postId))
    }

    override fun getPostsByAuthor(author: String): Flow<List<Post>> =
        MockData.postsFlow.map { it.filter { post -> post.author == author } }

    override fun getFavoritePosts(): Flow<List<Post>> =
        MockData.postsFlow.map { it.filter { post -> post.isFavorite } }

    override fun searchPosts(query: String): Flow<List<Post>> = flow {
        delay(500)
        emit(MockData.posts.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
        })
    }

    override suspend fun createPost(title: String, content: String, author: String, imageUrls: List<String>): Result<Post> {
        delay(1000)
        val newPost = Post(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            author = author,
            timestamp = System.currentTimeMillis(),
            imageUrls = imageUrls
        )
        MockData.posts.add(0, newPost)
        MockData.notifyPostsChanged()
        return Result.success(newPost)
    }

    override suspend fun updatePost(postId: String, title: String, content: String, imageUrls: List<String>): Result<Post> {
        delay(800)
        val index = MockData.posts.indexOfFirst { it.id == postId }
        if (index < 0) {
            return Result.failure(Exception("帖子不存在"))
        }
        val updatedPost = MockData.posts[index].copy(title = title, content = content, imageUrls = imageUrls)
        MockData.posts[index] = updatedPost
        MockData.notifyPostsChanged()
        return Result.success(updatedPost)
    }

    override suspend fun addComment(postId: String, content: String, author: String): Result<Comment> {
        delay(500)
        val newComment = Comment(
            id = System.currentTimeMillis().toString(),
            postId = postId,
            authorName = author,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        MockData.addComment(postId, newComment)
        return Result.success(newComment)
    }

    override suspend fun addReply(commentId: String, content: String, author: String): Result<Comment> {
        delay(500)
        val reply = Comment(
            id = System.currentTimeMillis().toString(),
            postId = "",
            authorName = author,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        MockData.addReply(commentId, reply)
        return Result.success(reply)
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        delay(500)
        MockData.posts.removeAll { it.id == postId }
        MockData.notifyPostsChanged()
        return Result.success(Unit)
    }

    override suspend fun toggleLike(postId: String): Result<Unit> {
        delay(200)
        MockData.posts.find { it.id == postId }?.let {
            if (it.isLiked) {
                it.likes--
                it.isLiked = false
            } else {
                it.likes++
                it.isLiked = true
            }
        }
        MockData.notifyPostsChanged()
        return Result.success(Unit)
    }

    override suspend fun toggleFavorite(postId: String): Result<Unit> {
        delay(200)
        MockData.posts.find { it.id == postId }?.let {
            it.isFavorite = !it.isFavorite
        }
        MockData.notifyPostsChanged()
        return Result.success(Unit)
    }
}
