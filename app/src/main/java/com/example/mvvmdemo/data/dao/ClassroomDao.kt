package com.example.mvvmdemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mvvmdemo.data.model.Classroom

@Dao
interface ClassroomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(classroom: Classroom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(classrooms: List<Classroom>)

    @Update
    suspend fun update(classroom: Classroom)

    @Query("SELECT * FROM classrooms")
    suspend fun getAllClassrooms(): List<Classroom>

    @Query("SELECT * FROM classrooms ORDER BY numberOfStudents ASC")
    suspend fun getClassroomsSortedByStudentsAsc(): List<Classroom>

    @Query("SELECT * FROM classrooms ORDER BY numberOfStudents DESC")
    suspend fun getClassroomsSortedByStudentsDesc(): List<Classroom>
}