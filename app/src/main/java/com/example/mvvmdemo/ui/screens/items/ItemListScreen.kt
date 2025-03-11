package com.example.mvvmdemo.ui.screens.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.ui.components.ActionButton
import com.example.mvvmdemo.viewmodel.AuthViewModel
import com.example.mvvmdemo.viewmodel.ItemViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(navController: NavController) {
    val db = AppDatabase.getDatabase(navController.context)
    val itemViewModel: ItemViewModel = viewModel(factory = ViewModelFactory(db))
    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(db, navController.context))
    val items by itemViewModel.items.collectAsState()

    val username = authViewModel.getCurrentUsername()
    val userId = authViewModel.getCurrentUserId()

    LaunchedEffect(userId) {
        if (userId != -1) {
            itemViewModel.loadItems(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("List product", style = MaterialTheme.typography.titleLarge)
                        username?.let {
                            Text(
                                text = "Welcome, $it",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    ActionButton(
                        text = "Logout",
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("itemList") { inclusive = true }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ActionButton(
                        text = "Create Item",
                        onClick = { navController.navigate("itemCreate") }
                    )
                }
            )
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("itemDetail/${item.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${item.name}")
                        Text("Price: ${item.price}")
                        Text("User ID: ${item.userId}")
                    }
                }
            }
        }
    }
}