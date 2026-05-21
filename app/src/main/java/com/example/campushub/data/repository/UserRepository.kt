package com.example.campushub.data.repository

import com.example.campushub.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户模块数据仓库接口
 */
interface UserRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String): Result<User>
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(nickname: String, signature: String, school: String, avatarUrl: String): Result<User>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    fun logout()
}
