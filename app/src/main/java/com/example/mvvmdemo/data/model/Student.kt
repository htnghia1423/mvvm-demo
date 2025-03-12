package com.example.mvvmdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: Int = 0,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String
)