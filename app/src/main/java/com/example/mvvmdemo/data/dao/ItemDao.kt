package com.example.mvvmdemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mvvmdemo.data.model.Item

@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>

    @Query("SELECT * FROM items WHERE userId = :userId")
    suspend fun getItemsByUserId(userId: Int): List<Item>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): Item?

    @Delete
    suspend fun delete(item: Item)

    @Update
    suspend fun update(item: Item)
}