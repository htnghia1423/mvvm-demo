package com.example.mvvmdemo.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.ui.components.ActionButton
import com.example.mvvmdemo.ui.components.CustomTextField
import com.example.mvvmdemo.ui.components.ErrorMessage
import com.example.mvvmdemo.ui.components.PasswordTextField
import com.example.mvvmdemo.viewmodel.AuthViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory

@Composable
fun LoginScreen(navController: NavController) {
    val db = AppDatabase.getDatabase(navController.context)
    val viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(db, navController.context))
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginResult by viewModel.loginResult.collectAsState()
    val authError by viewModel.authError.collectAsState()

    LaunchedEffect(Unit) {
        if (viewModel.isLoggedIn()) {
            navController.navigate("itemList") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(loginResult) {
        if (loginResult != null) {
            navController.navigate("itemList") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPasswordVisible = passwordVisible,
                onVisibilityToggle = { passwordVisible = !passwordVisible }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ActionButton(
                text = "Login",
                onClick = { viewModel.login(username, password) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton(
                text = "Go to Sign Up",
                onClick = { navController.navigate("signup") }
            )
            authError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorMessage(message = it)
            }
        }
    }
}