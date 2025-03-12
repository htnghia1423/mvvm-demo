package com.example.mvvmdemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmdemo.data.model.Student
import com.example.mvvmdemo.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    val students: StateFlow<List<Student>> = repository.students
    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _lastDeletedStudent = MutableStateFlow<Student?>(null)
    val lastDeletedStudent: StateFlow<Student?> = _lastDeletedStudent

    init {
        Log.d("StudentViewModel", "Initializing ViewModel")
        viewModelScope.launch {
            Log.d("StudentViewModel", "Fetching students in init")
            repository.fetchStudents()
        }
    }

    fun fetchStudents() {
        Log.d("StudentViewModel", "Manual fetchStudents called")
        viewModelScope.launch {
            repository.fetchStudents()
        }
    }

    fun addStudent(email: String, firstName: String, lastName: String, avatar: String) {
        viewModelScope.launch {
            val currentList = students.value
            val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
            val newStudent = Student(newId, email, firstName, lastName, avatar)
            repository.addStudent(newStudent)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            _lastDeletedStudent.value = student
            repository.deleteStudent(student.id)
        }
    }

    fun undoDelete() {
        _lastDeletedStudent.value?.let { student ->
            viewModelScope.launch {
                repository.addStudent(student)
                _lastDeletedStudent.value = null
            }
        }
    }

    fun sortStudents() {
        viewModelScope.launch {
            repository.sortStudents()
        }
    }
}