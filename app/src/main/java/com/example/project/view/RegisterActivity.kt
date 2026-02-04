package com.example.project.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.example.project.model.UserModel
import com.example.project.repository.UserRepoImplementation
import com.example.project.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteNestRegisterScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteNestRegisterScreen() {

    val userViewModel: UserViewModel = remember { UserViewModel(UserRepoImplementation()) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Date Picker State
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dob = format.format(Date(selectedDate))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB3DFFF),
                            Color(0xFF1E3A8A)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Create Account",
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Register to start using NoteNest",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(25.dp))

                // First Name
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Last Name
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date of Birth
                TextField(
                    value = dob,
                    onValueChange = { },
                    readOnly = true,
                    placeholder = { Text("Date of Birth (DD/MM/YYYY)") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Select Date"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible)
                                        R.drawable.baseline_visibility_off_24
                                    else
                                        R.drawable.baseline_visibility_24
                                ),
                                contentDescription = "Toggle Password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Register Button
                Button(
                    onClick = {
                        if (firstName.isBlank() || lastName.isBlank() || dob.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                        } else {
                            userViewModel.register(email.trim(), password.trim()) { success, message, userId ->
                                if (success) {
                                    val user = UserModel(
                                        userId = userId,
                                        firstName = firstName,
                                        lastName = lastName,
                                        dob = dob,
                                        email = email.trim(),
                                        password = password.trim()
                                    )

                                    userViewModel.addUserToDatabase(userId, user) { dbSuccess, dbMessage ->
                                        if (dbSuccess) {
                                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                            activity.finish()
                                        } else {
                                            Toast.makeText(context, dbMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Auth Failed: $message", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Register",
                        color = Color(0xFF1E3A8A),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Login")
                        }
                    },
                    modifier = Modifier.clickable {
                        val intent = Intent(
                            context,
                            LoginActivity::class.java
                        )
                        context.startActivity(intent)
                        activity.finish()
                    }
                )
            }
        }
    }
}