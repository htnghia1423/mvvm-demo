package com.example.mvvmdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmdemo.data.SessionManager
import com.example.mvvmdemo.data.model.User
import com.example.mvvmdemo.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    context: Context
) : ViewModel() {
    private val sessionManager = SessionManager(context)
    private val _loginResult = MutableStateFlow<User?>(null)
    val loginResult: StateFlow<User?> = _loginResult

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun signUp(username: String, password: String) {
        viewModelScope.launch {
            when {
                username.isBlank() -> _authError.value = "Username cannot be empty"
                password.isBlank() -> _authError.value = "Password cannot be empty"
                else -> {
                    try {
                        repository.signUp(User(username = username, password = password))
                        val user = repository.logIn(username, password)
                        user?.let {
                            _loginResult.value = it
                            sessionManager.saveUserSession(it.id, it.username)
                            _authError.value = null
                        }
                    } catch (e: Exception) {
                        _authError.value = e.message ?: "Sign up failed"
                    }
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            when {
                username.isBlank() -> _authError.value = "Username cannot be empty"
                password.isBlank() -> _authError.value = "Password cannot be empty"
                else -> {
                    val user = repository.logIn(username, password)
                    if (user != null) {
                        _loginResult.value = user
                        sessionManager.saveUserSession(user.id, user.username)
                        _authError.value = null
                    } else {
                        _authError.value = "Invalid username or password"
                    }
                }
            }
        }
    }

    fun logout() {
        _loginResult.value = null
        sessionManager.clearSession()
        _authError.value = null
    }

    fun setAuthError(error: String?) {
        _authError.value = error
    }

    fun getCurrentUserId(): Int = sessionManager.getUserId()
    fun getCurrentUsername(): String? = sessionManager.getUsername()
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}