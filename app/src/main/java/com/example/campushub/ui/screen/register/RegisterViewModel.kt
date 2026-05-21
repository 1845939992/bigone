package com.example.campushub.ui.screen.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.data.repository.impl.MockUserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    // 使用单例模式获取仓库实例，确保状态同步
    private val userRepository: UserRepository = MockUserRepository.getInstance()
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<RegisterEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onRegisterClick() {
        if (username.isBlank() || password.isBlank()) {
            sendError("用户名和密码不能为空")
            return
        }
        if (password != confirmPassword) {
            sendError("两次输入的密码不一致")
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = userRepository.register(username, password)
            isLoading = false
            
            result.fold(
                onSuccess = { _eventFlow.emit(RegisterEvent.RegisterSuccess) },
                onFailure = { _eventFlow.emit(RegisterEvent.ShowError(it.message ?: "注册失败")) }
            )
        }
    }

    private fun sendError(msg: String) {
        viewModelScope.launch { _eventFlow.emit(RegisterEvent.ShowError(msg)) }
    }

    sealed class RegisterEvent {
        object RegisterSuccess : RegisterEvent()
        data class ShowError(val message: String) : RegisterEvent()
    }
}
