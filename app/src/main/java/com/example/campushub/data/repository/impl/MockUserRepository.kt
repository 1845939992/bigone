package com.example.campushub.data.repository.impl

import com.example.campushub.data.MockData
import com.example.campushub.data.model.User
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.utils.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockUserRepository private constructor() : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    companion object {
        @Volatile
        private var instance: MockUserRepository? = null

        fun getInstance(): MockUserRepository {
            return instance ?: synchronized(this) {
                instance ?: MockUserRepository().also { instance = it }
            }
        }
    }

    override suspend fun login(username: String, password: String): Result<User> {
        delay(1000)

        val user = MockData.users.find { it.username == username }
        val savedPassword = MockData.userPasswords[username]

        return if (user != null && savedPassword == password) {
            _currentUser.value = user
            MockData.currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("用户名或密码错误"))
        }
    }

    override suspend fun register(username: String, password: String): Result<User> {
        delay(1000)

        if (MockData.users.any { it.username == username }) {
            return Result.failure(Exception("注册失败：用户名已存在"))
        }

        return if (username.length >= 3 && password.length >= 6) {
            val newUser = User(
                id = System.currentTimeMillis().toString(),
                username = username,
                nickname = "新用户_$username",
                avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=$username"
            )

            MockData.users.add(newUser)
            MockData.userPasswords[username] = password
            PreferencesManager.saveRegisteredUser(newUser, password)

            _currentUser.value = newUser
            MockData.currentUser = newUser
            Result.success(newUser)
        } else {
            Result.failure(Exception("注册失败：用户名需不少于3位，密码不少于6位"))
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun updateProfile(nickname: String, signature: String, school: String, avatarUrl: String): Result<User> {
        delay(800)
        val current = _currentUser.value
            ?: return Result.failure(Exception("用户未登录"))

        val updatedUser = current.copy(
            nickname = nickname,
            signature = signature,
            school = school,
            avatarUrl = avatarUrl
        )

        val index = MockData.users.indexOfFirst { it.id == current.id }
        if (index >= 0) {
            MockData.users[index] = updatedUser
        }
        _currentUser.value = updatedUser
        MockData.currentUser = updatedUser

        val password = MockData.userPasswords[current.username] ?: ""
        PreferencesManager.saveRegisteredUser(updatedUser, password)

        return Result.success(updatedUser)
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        delay(800)
        val current = _currentUser.value
            ?: return Result.failure(Exception("用户未登录"))

        val savedPassword = MockData.userPasswords[current.username]
        if (savedPassword != oldPassword) {
            return Result.failure(Exception("原密码错误"))
        }
        if (newPassword.length < 6) {
            return Result.failure(Exception("新密码不能少于6位"))
        }
        MockData.userPasswords[current.username] = newPassword

        PreferencesManager.saveRegisteredUser(current, newPassword)

        return Result.success(Unit)
    }

    override fun logout() {
        _currentUser.value = null
        MockData.currentUser = null
        PreferencesManager.clearLoginState()
    }
}
