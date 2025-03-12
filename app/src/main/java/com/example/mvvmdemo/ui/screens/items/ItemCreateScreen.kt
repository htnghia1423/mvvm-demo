package com.example.mvvmdemo.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.ui.components.ActionButton
import com.example.mvvmdemo.ui.components.BackButton
import com.example.mvvmdemo.ui.components.CustomTextField
import com.example.mvvmdemo.ui.components.ErrorMessage
import com.example.mvvmdemo.viewmodel.AuthViewModel
import com.example.mvvmdemo.viewmodel.ItemViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCreateScreen(navController: NavController) {
    val db = AppDatabase.getDatabase(navController.context)
    val itemViewModel: ItemViewModel = viewModel(factory = ViewModelFactory(db))
    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(db, navController.context))
    val itemError by itemViewModel.itemError.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    val createSuccess by itemViewModel.createSuccess.collectAsState()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val userId = authViewModel.getCurrentUserId()

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            delay(100)
            navController.popBackStack()
            itemViewModel.resetCreateSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Item") },
                navigationIcon = {
                    BackButton(onClick = { navController.popBackStack() })
                }
            )
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            CustomTextField(
                value = name,
                onValueChange = {
                    name = it
                    itemViewModel.setItemError(null)
                },
                label = "Name"
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = price,
                onValueChange = {
                    price = it
                    itemViewModel.setItemError(null)
                },
                label = "Price"
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = description,
                onValueChange = {
                    description = it
                    itemViewModel.setItemError(null)
                },
                label = "Description"
            )
            Spacer(modifier = Modifier.height(16.dp))
            ActionButton(
                text = "Create",
                onClick = {
                    if (userId != -1) {
                        itemViewModel.createItem(name, price, description, userId)
                    }
                },
                enabled = !isLoading
            )
            itemError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorMessage(message = it)
            }
        }
    }
}