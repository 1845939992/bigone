package com.example.campushub.data.repository.impl

import com.example.campushub.data.api.ApiService
import com.example.campushub.data.api.ChangePasswordRequest
import com.example.campushub.data.api.LoginRequest
import com.example.campushub.data.api.RegisterRequest
import com.example.campushub.data.api.UpdateProfileRequest
import com.example.campushub.data.model.User
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.utils.NetworkUtils
import com.example.campushub.utils.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

class ApiUserRepository(private val api: ApiService) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    private suspend fun safeCall(block: suspend () -> Result<User>): Result<User> {
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

    override suspend fun login(username: String, password: String): Result<User> = safeCall {
        val response = api.login(LoginRequest(username, password))
        if (response.code == 0 && response.data != null) {
            _currentUser.value = response.data
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "登录失败" }))
        }
    }

    override suspend fun register(username: String, password: String): Result<User> = safeCall {
        val response = api.register(RegisterRequest(username, password))
        if (response.code == 0 && response.data != null) {
            _currentUser.value = response.data
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "注册失败" }))
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun updateProfile(
        nickname: String,
        signature: String,
        school: String,
        avatarUrl: String
    ): Result<User> = safeCall {
        val response = api.updateProfile(
            UpdateProfileRequest(nickname, signature, school, avatarUrl)
        )
        if (response.code == 0 && response.data != null) {
            _currentUser.value = response.data
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message.ifBlank { "更新失败" }))
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit> {
        if (!NetworkUtils.isNetworkAvailable()) {
            return Result.failure(IOException("网络不可用，请检查网络连接"))
        }
        return try {
            val response = api.changePassword(
                ChangePasswordRequest(oldPassword, newPassword)
            )
            if (response.code == 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message.ifBlank { "修改失败" }))
            }
        } catch (e: IOException) {
            Result.failure(IOException("网络请求失败，请稍后重试"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "服务异常，请稍后重试"))
        }
    }

    override fun logout() {
        _currentUser.value = null
        PreferencesManager.clearLoginState()
    }
}
