package com.example.mvvmdemo.data.repository

import com.example.mvvmdemo.data.dao.ItemDao
import com.example.mvvmdemo.data.model.Item

class ItemRepository(private val itemDao: ItemDao) {
    suspend fun insertItem(item: Item) {
        return itemDao.insert(item)
    }

    suspend fun getAllItems(): List<Item> {
        return itemDao.getAllItems()
    }

    suspend fun getItemById(id: Int): Item? {
        return itemDao.getItemById(id)
    }

    suspend fun deleteItem(item: Item) {
        return itemDao.delete(item)
    }

    suspend fun getItemsByUserId(userId: Int): List<Item> {
        return itemDao.getItemsByUserId(userId)
    }
}