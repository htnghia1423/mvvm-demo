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

    private val _itemError = MutableStateFlow<String?>(null)
    val itemError: StateFlow<String?> = _itemError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    fun createItem(name: String, price: String, description: String, userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when {
                name.isBlank() -> _itemError.value = "Name cannot be empty"
                name.length < 3 -> _itemError.value = "Name must be at least 3 characters"
                price.isBlank() -> _itemError.value = "Price cannot be empty"
                description.isBlank() -> _itemError.value = "Description cannot be empty"
                else -> {
                    val priceDouble = price.toDoubleOrNull()
                    if (priceDouble == null || priceDouble < 0) {
                        _itemError.value = "Price must be a valid non-negative number"
                    } else {
                        val newItem = Item(
                            name = name,
                            price = priceDouble,
                            description = description,
                            userId = userId
                        )
                        repository.insertItem(newItem)
                        loadItems(userId)
                        _itemError.value = null
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun deleteItem(itemId: Int, userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val item = repository.getItemById(itemId)
            if (item != null) {
                repository.deleteItem(item)
                loadItems(userId)
            }
            _isLoading.value = false
        }
    }

    fun updateItem(itemId: Int, name: String, price: String, description: String, userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when {
                name.isBlank() -> _itemError.value = "Name cannot be empty"
                name.length < 3 -> _itemError.value = "Name must be at least 3 characters"
                price.isBlank() -> _itemError.value = "Price cannot be empty"
                description.isBlank() -> _itemError.value = "Description cannot be empty"
                else -> {
                    val priceDouble = price.toDoubleOrNull()
                    if (priceDouble == null || priceDouble < 0) {
                        _itemError.value = "Price must be a valid non-negative number"
                    } else {
                        val item = repository.getItemById(itemId)?.copy(
                            name = name,
                            price = priceDouble,
                            description = description,
                            userId = userId
                        )
                        if (item != null) {
                            repository.updateItem(item)
                            loadItems(userId)
                            _selectedItem.value = item
                            _itemError.value = null
                        }
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun setItemError(error: String?) {
        _itemError.value = error
    }
}