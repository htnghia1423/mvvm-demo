package com.example.mvvmdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmdemo.data.model.Item
import com.example.mvvmdemo.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _selectedItem = MutableStateFlow<Item?>(null)
    val selectedItem: StateFlow<Item?> = _selectedItem

    fun loadItems(userId: Int) {
        viewModelScope.launch {
            _items.value = repository.getItemsByUserId(userId)
        }
    }

    fun loadItemDetail(itemId: Int) {
        viewModelScope.launch {
            _selectedItem.value = repository.getItemById(itemId)
        }
    }

    fun createItem(name: String, price: Double, description: String, userId: Int) {
        viewModelScope.launch {
            val newItem = Item(name = name, price = price, description = description, userId = userId)
            repository.insertItem(newItem)
            loadItems(userId)
        }
    }

    fun deleteItem(itemId: Int, userId: Int) {
        viewModelScope.launch {
            val item = repository.getItemById(itemId)
            if (item != null) {
                repository.deleteItem(item)
                loadItems(userId)
            }
        }
    }
}