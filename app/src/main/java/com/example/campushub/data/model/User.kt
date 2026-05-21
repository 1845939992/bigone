package com.example.campushub.data.model

/**
 * 用户信息数据模型
 */
data class User(
    val id: String,
    val username: String,
    val nickname: String = "",
    val avatarUrl: String = "",
    val signature: String = "",
    val school: String = ""
)
