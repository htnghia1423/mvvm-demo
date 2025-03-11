package com.example.mvvmdemo.data.repository

import com.example.mvvmdemo.data.dao.UserDao
import com.example.mvvmdemo.data.model.User
import org.mindrot.jbcrypt.BCrypt

class AuthRepository(private val userDao: UserDao) {
    suspend fun signUp(user: User) {
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val hashedUser = user.copy(password = hashedPassword)
        try {
            userDao.insert(hashedUser)
        } catch (e: Exception) {
            throw Exception("User name already exists")
        }
    }

    suspend fun logIn(username: String, password: String): User? {
        val user = userDao.getUser(username)
        return if (user != null && BCrypt.checkpw(password, user.password)) {
            user
        } else {
            null
        }
    }
}