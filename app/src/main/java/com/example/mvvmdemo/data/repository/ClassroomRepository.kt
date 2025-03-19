package com.example.mvvmdemo.data.repository

import android.util.Log
import com.example.mvvmdemo.data.dao.ClassroomDao
import com.example.mvvmdemo.data.model.Classroom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("classrooms")
    suspend fun getClassrooms(): List<Classroom>
}

class ClassroomRepository(private val classroomDao: ClassroomDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://67d8dbba00348dd3e2a871ae.mockapi.io/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    private val _classrooms = MutableStateFlow<List<Classroom>>(emptyList())
    val classrooms: StateFlow<List<Classroom>> = _classrooms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun fetchClassrooms() {
        _isLoading.value = true
        try {
            Log.d("ClassroomRepository", "Fetching classrooms from API...")
            val response = withContext(Dispatchers.IO) { apiService.getClassrooms() }
            Log.d("ClassroomRepository", "API response: $response")
            _classrooms.value = response
            withContext(Dispatchers.IO) {
                response.forEach {
                    classroomDao.insert(it)
                    Log.d("ClassroomRepository", "Inserted classroom: ${it.id}")
                }
            }
            Log.d("ClassroomRepository", "Updated _classrooms with ${response.size} items")
        } catch (e: Exception) {
            Log.e("ClassroomRepository", "Error fetching classrooms: ${e.message}", e)
            getClassroomsFromDb()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun getClassroomsFromDb() {
        _isLoading.value = true
        try {
            val dbClassrooms = withContext(Dispatchers.IO) { classroomDao.getAllClassrooms() }
            Log.d("ClassroomRepository", "Loaded from DB: ${dbClassrooms.size} classrooms")
            _classrooms.value = dbClassrooms
            if (dbClassrooms.isEmpty()) {
                Log.w("ClassroomRepository", "DB is empty, no classrooms to load")
            }
        } catch (e: Exception) {
            Log.e("ClassroomRepository", "Error loading from DB: ${e.message}", e)
            _classrooms.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addClassroom(classroom: Classroom) {
        withContext(Dispatchers.IO) { classroomDao.insert(classroom) }
        getClassroomsFromDb()
    }

    suspend fun updateClassroom(classroom: Classroom) {
        withContext(Dispatchers.IO) { classroomDao.update(classroom) }
        getClassroomsFromDb()
    }

    suspend fun updateClassrooms(classrooms: List<Classroom>) {
        withContext(Dispatchers.IO) { classroomDao.insertAll(classrooms) }
        _classrooms.value = classrooms
    }
}