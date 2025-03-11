package com.example.mvvmdemo.ui.screens.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.ui.components.ActionButton
import com.example.mvvmdemo.ui.components.BackButton
import com.example.mvvmdemo.viewmodel.AuthViewModel
import com.example.mvvmdemo.viewmodel.ItemViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(navController: NavController, itemId: Int) {
    val db = AppDatabase.getDatabase(navController.context)
    val itemViewModel: ItemViewModel = viewModel(factory = ViewModelFactory(db))
    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(db, navController.context))
    val item by itemViewModel.selectedItem.collectAsState()
    val userId = authViewModel.getCurrentUserId()

    LaunchedEffect(itemId) {
        itemViewModel.loadItemDetail(itemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Detail") },
                navigationIcon = {
                    BackButton(onClick = { navController.popBackStack() })
                }
            )
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { padding ->
        item?.let {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Tên: ${it.name}")
                Text("Giá: ${it.price}")
                Text("Mô tả: ${it.description}")
                Text("User ID: ${it.userId}")
                Spacer(modifier = Modifier.height(16.dp))
                ActionButton(
                    text = "Update",
                    onClick = { navController.navigate("itemUpdate/${it.id}") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ActionButton(
                    text = "Delete",
                    onClick = {
                        if (userId != -1) {
                            itemViewModel.deleteItem(it.id, userId)
                            navController.popBackStack()
                        }
                    },
                    backgroundColor = MaterialTheme.colorScheme.error
                )
            }
        } ?: Text("Loading...", modifier = Modifier.padding(padding))
    }
}