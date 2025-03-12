package com.example.mvvmdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.data.repository.AuthRepository
import com.example.mvvmdemo.data.repository.ItemRepository
import com.example.mvvmdemo.data.repository.StudentRepository

class ViewModelFactory(
    private val db: AppDatabase,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                context?.let {
                    AuthViewModel(AuthRepository(db.userDao()), it) as T
                } ?: throw IllegalArgumentException("Context is required for AuthViewModel")
            }
            modelClass.isAssignableFrom(ItemViewModel::class.java) -> {
                ItemViewModel(ItemRepository(db.itemDao())) as T
            }
            modelClass.isAssignableFrom(StudentViewModel::class.java) -> {
                StudentViewModel(StudentRepository(db.studentDao())) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}