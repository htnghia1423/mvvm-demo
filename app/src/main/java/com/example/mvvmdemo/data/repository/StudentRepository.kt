package com.example.mvvmdemo.data.repository

import android.util.Log
import com.example.mvvmdemo.data.dao.StudentDao
import com.example.mvvmdemo.data.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    suspend fun getStudents(@Query("page") page: Int): StudentResponse
}

data class StudentResponse(val data: List<Student>)

class StudentRepository(private val studentDao: StudentDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://reqres.in/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(ApiService::class.java)

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun fetchStudents() {
        _isLoading.value = true
        try {
            Log.d("StudentRepository", "Fetching students from API...")
            val response = withContext(Dispatchers.IO) { apiService.getStudents(1) }
            Log.d("StudentRepository", "API response: ${response.data}")
            _students.value = response.data
            withContext(Dispatchers.IO) {
                response.data.forEach {
                    studentDao.insert(it)
                    Log.d("StudentRepository", "Inserted student: ${it.id}")
                }
            }
            Log.d("StudentRepository", "Updated _students with ${response.data.size} items")
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error fetching students: ${e.message}", e)
            getStudentsFromDb()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun getStudentsFromDb() {
        _isLoading.value = true
        try {
            val dbStudents = withContext(Dispatchers.IO) { studentDao.getAllStudents() }
            Log.d("StudentRepository", "Loaded from DB: ${dbStudents.size} students")
            _students.value = dbStudents
            if (dbStudents.isEmpty()) {
                Log.w("StudentRepository", "DB is empty, no students to load")
            }
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error loading from DB: ${e.message}", e)
            _students.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addStudent(student: Student) {
        withContext(Dispatchers.IO) { studentDao.insert(student) }
        getStudentsFromDb()
    }

    suspend fun deleteStudent(studentId: Int) {
        withContext(Dispatchers.IO) { studentDao.deleteById(studentId) }
        getStudentsFromDb()
    }

    suspend fun sortStudents() {
        val sortedList = _students.value.sortedBy { "${it.first_name} ${it.last_name}" }
        withContext(Dispatchers.IO) { studentDao.insertAll(sortedList) }
        _students.value = sortedList
    }
}