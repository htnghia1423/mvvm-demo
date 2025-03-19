package com.example.mvvmdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmdemo.data.model.Classroom
import com.example.mvvmdemo.data.repository.ClassroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassroomViewModel(private val repository: ClassroomRepository) : ViewModel() {
    val classrooms: StateFlow<List<Classroom>> = repository.classrooms
    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _filteredClassrooms = MutableStateFlow<List<Classroom>>(emptyList())
    val filteredClassrooms: StateFlow<List<Classroom>> = _filteredClassrooms

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess

    init {
        fetchClassrooms()
    }

    fun fetchClassrooms() {
        viewModelScope.launch {
            repository.fetchClassrooms()
            _filteredClassrooms.value = repository.classrooms.value // Initialize with full list
        }
    }

    fun addClassroom(name: String, numberOfStudents: Int, isActive: Boolean) {
        viewModelScope.launch {
            val newClassroom = Classroom(0, name, numberOfStudents, isActive)
            repository.addClassroom(newClassroom)
            _operationSuccess.value = "Classroom added successfully"
        }
    }

    fun updateClassroomStudents(classroomId: Int, newNumberOfStudents: Int) {
        viewModelScope.launch {
            val updatedClassroom = classrooms.value.find { it.id == classroomId }?.copy(numberOfStudents = newNumberOfStudents)
            if (updatedClassroom != null) {
                repository.updateClassroom(updatedClassroom)
                _operationSuccess.value = "Classroom updated successfully"
            }
        }
    }

    fun sortClassroomsByStudentsAsc() {
        viewModelScope.launch {
            repository.getClassroomsSortedByStudentsAsc()
        }
    }

    fun sortClassroomsByStudentsDesc() {
        viewModelScope.launch {
            repository.getClassroomsSortedByStudentsDesc()
        }
    }

    fun filterClassrooms(query: String) {
        _filteredClassrooms.value = if (query.isEmpty()) {
            classrooms.value
        } else {
            classrooms.value.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun resetOperationSuccess() {
        _operationSuccess.value = null
    }
}