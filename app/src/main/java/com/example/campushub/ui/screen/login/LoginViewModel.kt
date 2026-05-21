package com.example.campushub.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.data.repository.impl.MockUserRepository
import com.example.campushub.utils.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository = MockUserRepository.getInstance()
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var rememberPassword by mutableStateOf(false)
        private set

    var autoLogin by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isAutoLoggingIn by mutableStateOf(false)
        private set

    private val _eventFlow = MutableSharedFlow<LoginEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        if (PreferencesManager.isRememberPassword()) {
            username = PreferencesManager.getSavedUsername()
            password = PreferencesManager.getSavedPassword()
            rememberPassword = true
        }
        if (PreferencesManager.isAutoLogin()) {
            autoLogin = true
        }
    }

    fun updateUsername(value: String) {
        username = value
    }

    fun updatePassword(value: String) {
        password = value
    }

    fun updateRememberPassword(value: Boolean) {
        rememberPassword = value
        if (!value) {
            autoLogin = false
        }
    }

    fun updateAutoLogin(value: Boolean) {
        autoLogin = value
    }

    fun onAutoLogin() {
        if (!PreferencesManager.isAutoLogin()) return
        val savedUsername = PreferencesManager.getSavedUsername()
        val savedPassword = PreferencesManager.getSavedPassword()
        if (savedUsername.isBlank() || savedPassword.isBlank()) return

        username = savedUsername
        password = savedPassword
        rememberPassword = true

        viewModelScope.launch {
            isAutoLoggingIn = true
            isLoading = true
            delay(800)
            val result = userRepository.login(savedUsername, savedPassword)
            isLoading = false
            isAutoLoggingIn = false

            result.fold(
                onSuccess = {
                    PreferencesManager.saveCredentials(savedUsername, savedPassword, rememberPassword = true, autoLogin = true)
                    _eventFlow.emit(LoginEvent.AutoLoginSuccess)
                },
                onFailure = {
                    _eventFlow.emit(LoginEvent.ShowError("自动登录失败，请重新登录"))
                }
            )
        }
    }

    fun onLoginClick() {
        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(LoginEvent.ShowError("用户名和密码不能为空"))
            }
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = userRepository.login(username, password)
            isLoading = false

            result.fold(
                onSuccess = {
                    PreferencesManager.saveCredentials(username, password, rememberPassword, autoLogin)
                    _eventFlow.emit(LoginEvent.LoginSuccess)
                },
                onFailure = {
                    _eventFlow.emit(LoginEvent.ShowError(it.message ?: "登录失败"))
                }
            )
        }
    }

    sealed class LoginEvent {
        object LoginSuccess : LoginEvent()
        object AutoLoginSuccess : LoginEvent()
        data class ShowError(val message: String) : LoginEvent()
    }
}
