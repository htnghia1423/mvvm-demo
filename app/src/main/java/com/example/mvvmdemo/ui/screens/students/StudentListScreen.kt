package com.example.mvvmdemo.ui.screens.students

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.data.model.Student
import com.example.mvvmdemo.ui.components.ActionButton
import com.example.mvvmdemo.viewmodel.AuthViewModel
import com.example.mvvmdemo.viewmodel.StudentViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(navController: NavController) {
    val db = AppDatabase.getDatabase(navController.context)
    val viewModel: StudentViewModel = viewModel(factory = ViewModelFactory(db))
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lastDeletedStudent by viewModel.lastDeletedStudent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(db, navController.context))
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("") }

    LaunchedEffect(lastDeletedStudent) {
        lastDeletedStudent?.let {
            val result = snackbarHostState.showSnackbar(
                message = "Student ${it.first_name} ${it.last_name} deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("StudentListScreen", "LaunchedEffect triggered, fetching students")
        viewModel.fetchStudents()
    }

    Log.d("StudentListScreen", "Rendering: Students size: ${students.size}, isLoading: $isLoading")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
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
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = avatar,
                onValueChange = { avatar = it },
                label = { Text("Avatar URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (email.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank()) {
                            viewModel.addStudent(email, firstName, lastName, avatar)
                            Toast.makeText(context, "Student added", Toast.LENGTH_SHORT).show()
                            email = ""
                            firstName = ""
                            lastName = ""
                            avatar = ""
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Add")
                }

                Button(
                    onClick = { viewModel.sortStudents() },
                    enabled = !isLoading
                ) {
                    Text("Sort")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (students.isEmpty()) {
                    Text("No students available", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        items(students) { student ->
                            StudentItem(
                                student = student,
                                onDelete = { viewModel.deleteStudent(student) } // Câu 3
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentItem(student: Student, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = student.avatar,
                contentDescription = "Avatar of ${student.first_name} ${student.last_name}",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("${student.first_name} ${student.last_name}", style = MaterialTheme.typography.bodyLarge)
                Text(student.email, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}