package com.example.campushub.data.api

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

data class LoginRequest(val username: String, val password: String)

data class RegisterRequest(val username: String, val password: String)

data class UpdateProfileRequest(
    val nickname: String,
    val signature: String,
    val school: String,
    val avatarUrl: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class CreatePostRequest(
    val title: String,
    val content: String,
    val author: String,
    val imageUrls: List<String> = emptyList()
)

data class UpdatePostRequest(
    val title: String,
    val content: String,
    val imageUrls: List<String> = emptyList()
)

data class AddCommentRequest(
    val content: String,
    val author: String
)
