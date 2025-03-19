package com.example.mvvmdemo.viewmodel

import android.util.Log
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

    private val _lastDeletedClassroom = MutableStateFlow<Classroom?>(null)
    val lastDeletedClassroom: StateFlow<Classroom?> = _lastDeletedClassroom

    init {
        Log.d("ClassroomViewModel", "Initializing ViewModel")
        viewModelScope.launch {
            Log.d("ClassroomViewModel", "Fetching classrooms in init")
            repository.fetchClassrooms()
        }
    }

    fun fetchClassrooms() {
        Log.d("ClassroomViewModel", "Manual fetchClassrooms called")
        viewModelScope.launch {
            repository.fetchClassrooms()
        }
    }

    fun addClassroom(name: String, numberOfStudents: Int, isActive: Boolean) {
        viewModelScope.launch {
            val currentList = classrooms.value
            val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
            val newClassroom = Classroom(newId, name, numberOfStudents, isActive)
            repository.addClassroom(newClassroom)
        }
    }

    fun updateClassroomStudents(classroomId: Int, newNumberOfStudents: Int) {
        viewModelScope.launch {
            val updatedClassrooms = classrooms.value.map { classroom ->
                if (classroom.id == classroomId) {
                    classroom.copy(numberOfStudents = newNumberOfStudents)
                } else {
                    classroom
                }
            }
            repository.updateClassrooms(updatedClassrooms)
        }
    }
}