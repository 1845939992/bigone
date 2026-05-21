package com.example.campushub.data.model

/**
 * 帖子数据模型
 */
data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val timestamp: Long,
    val imageUrls: List<String> = emptyList(),
    var likes: Int = 0,
    var isLiked: Boolean = false,
    var isFavorite: Boolean = false
)
