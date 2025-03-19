package com.example.mvvmdemo.ui.screens.classrooms

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mvvmdemo.data.database.AppDatabase
import com.example.mvvmdemo.data.model.Classroom
import com.example.mvvmdemo.viewmodel.ClassroomViewModel
import com.example.mvvmdemo.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomListScreen(navController: NavController) {
    val db = AppDatabase.getDatabase(navController.context)
    val viewModel: ClassroomViewModel = viewModel(factory = ViewModelFactory(db))
    val classrooms by viewModel.filteredClassrooms.collectAsState() // Observe filteredClassrooms
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var numberOfStudents by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedClassroom by remember { mutableStateOf<Classroom?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val operationSuccess by viewModel.operationSuccess.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchClassrooms()
        viewModel.filterClassrooms("") // Initialize with full list
    }

    LaunchedEffect(searchQuery) {
        viewModel.filterClassrooms(searchQuery)
    }

    LaunchedEffect(operationSuccess) {
        operationSuccess?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetOperationSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Classroom List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Students Ascending") },
                                onClick = {
                                    viewModel.sortClassroomsByStudentsAsc()
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Students Descending") },
                                onClick = {
                                    viewModel.sortClassroomsByStudentsDesc()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Classroom") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Classroom Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = numberOfStudents,
                onValueChange = { numberOfStudents = it },
                label = { Text("Number of Students") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active")
                Switch(
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && numberOfStudents.isNotBlank()) {
                        viewModel.addClassroom(name, numberOfStudents.toInt(), isActive)
                        name = ""
                        numberOfStudents = ""
                        isActive = true
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading
            ) {
                Text("Add Classroom")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (classrooms.isEmpty()) {
                    Text("No classrooms available", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        items(classrooms) { classroom ->
                            ClassroomItem(
                                classroom = classroom,
                                onClick = {
                                    selectedClassroom = classroom
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedClassroom != null) {
        UpdateClassroomDialog(
            classroom = selectedClassroom!!,
            onDismiss = { showDialog = false },
            onUpdate = { newNumberOfStudents ->
                viewModel.updateClassroomStudents(selectedClassroom!!.id, newNumberOfStudents)
                showDialog = false
            }
        )
    }
}

@Composable
fun ClassroomItem(classroom: Classroom, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(classroom.name, style = MaterialTheme.typography.bodyLarge)
                Text("Number of Students: ${classroom.numberOfStudents}", style = MaterialTheme.typography.bodyMedium)
                Text("Active: ${classroom.isActive}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun UpdateClassroomDialog(classroom: Classroom, onDismiss: () -> Unit, onUpdate: (Int) -> Unit) {
    var newNumberOfStudents by remember { mutableStateOf(classroom.numberOfStudents.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Number of Students") },
        text = {
            OutlinedTextField(
                value = newNumberOfStudents,
                onValueChange = { newNumberOfStudents = it },
                label = { Text("Number of Students") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newNumberOfStudents.isNotBlank()) {
                        onUpdate(newNumberOfStudents.toInt())
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}