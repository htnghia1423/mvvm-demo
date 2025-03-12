package com.example.mvvmdemo.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvvmdemo.data.SessionManager
import com.example.mvvmdemo.ui.screens.items.ItemDetailScreen
import com.example.mvvmdemo.ui.screens.items.ItemListScreen
import com.example.mvvmdemo.ui.screens.auth.LoginScreen
import com.example.mvvmdemo.ui.screens.auth.SignUpScreen
import com.example.mvvmdemo.ui.screens.items.ItemCreateScreen
import com.example.mvvmdemo.ui.screens.items.ItemUpdateScreen
import com.example.mvvmdemo.ui.screens.students.StudentListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        setContent {
            MaterialTheme {
                AppNavigation(sessionManager)
            }
        }
    }
}

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val startDestination = if (sessionManager.isLoggedIn()) "studentList" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("itemList") { ItemListScreen(navController) }
        composable("itemDetail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            ItemDetailScreen(navController, itemId)
        }
        composable("itemCreate") { ItemCreateScreen(navController) }
        composable("itemUpdate/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            ItemUpdateScreen(navController, itemId)
        }
        composable("studentList") { StudentListScreen(navController) }
    }
}