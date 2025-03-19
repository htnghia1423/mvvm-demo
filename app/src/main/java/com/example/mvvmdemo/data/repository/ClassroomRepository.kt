package com.example.mvvmdemo.data.repository

import com.example.mvvmdemo.data.dao.ClassroomDao
import com.example.mvvmdemo.data.model.Classroom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class ClassroomRepository(private val classroomDao: ClassroomDao) {
    private val _classrooms = MutableStateFlow<List<Classroom>>(emptyList())
    val classrooms: StateFlow<List<Classroom>> = _classrooms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun fetchClassrooms() {
        _isLoading.value = true
        try {
            val dbClassrooms = withContext(Dispatchers.IO) { classroomDao.getAllClassrooms() }
            _classrooms.value = dbClassrooms
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addClassroom(classroom: Classroom) {
        withContext(Dispatchers.IO) { classroomDao.insert(classroom) }
        fetchClassrooms()
    }

    suspend fun updateClassroom(classroom: Classroom) {
        withContext(Dispatchers.IO) { classroomDao.update(classroom) }
        fetchClassrooms()
    }

    suspend fun getClassroomsSortedByStudentsAsc() {
        _isLoading.value = true
        try {
            val sortedClassrooms = withContext(Dispatchers.IO) { classroomDao.getClassroomsSortedByStudentsAsc() }
            _classrooms.value = sortedClassrooms
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun getClassroomsSortedByStudentsDesc() {
        _isLoading.value = true
        try {
            val sortedClassrooms = withContext(Dispatchers.IO) { classroomDao.getClassroomsSortedByStudentsDesc() }
            _classrooms.value = sortedClassrooms
        } finally {
            _isLoading.value = false
        }
    }
}