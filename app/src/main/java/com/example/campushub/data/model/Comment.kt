package com.example.campushub.data.model

/**
 * 评论数据模型
 */
data class Comment(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorAvatar: String = "",
    val content: String,
    val timestamp: Long,
    val replies: List<Comment> = emptyList() // 支持二级回复
)
