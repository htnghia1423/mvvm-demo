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
import com.example.mvvmdemo.ui.components.BackButton
import com.example.mvvmdemo.ui.components.CustomTextField
import com.example.mvvmdemo.ui.components.ErrorMessage
import com.example.mvvmdemo.ui.components.PasswordTextField
import com.example.mvvmdemo.viewmodel.AuthViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val db = AppDatabase.getDatabase(navController.context)
    val viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(db, navController.context))
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val authError by viewModel.authError.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(Unit) {
        if (viewModel.isLoggedIn()) {
            navController.navigate("itemList") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    LaunchedEffect(loginResult) {
        if (loginResult != null) {
            navController.navigate("itemList") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up") },
                navigationIcon = {
                    BackButton(onClick = { navController.popBackStack() })
                }
            )
        },
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
                text = "Sign Up",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            CustomTextField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.setAuthError(null)
                },
                label = "Username"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.setAuthError(null)
                },
                label = "Password",
                isPasswordVisible = passwordVisible,
                onVisibilityToggle = { passwordVisible = !passwordVisible }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    viewModel.setAuthError(null)
                },
                label = "Confirm Password",
                isPasswordVisible = confirmPasswordVisible,
                onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ActionButton(
                text = "Sign Up",
                onClick = {
                    if (confirmPassword.isBlank()) {
                        viewModel.setAuthError("Confirm Password cannot be empty")
                    } else if (password != confirmPassword) {
                        viewModel.setAuthError("Passwords do not match")
                    } else {
                        viewModel.signUp(username, password)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton(
                text = "Back to Login",
                onClick = { navController.navigate("login") }
            )
            authError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorMessage(message = it)
            }
        }
    }
}