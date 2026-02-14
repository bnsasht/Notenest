package com.example.project.view

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.project.model.UserModel
import com.example.project.viewmodel.UserViewModel
import com.example.project.view.LoginActivity

@Composable
fun SettingsScreen(userViewModel: UserViewModel) {

    val context = LocalContext.current
    val firebaseUser = userViewModel.getCurrentUser()
    val currentUserId = firebaseUser?.uid ?: ""

    val user by userViewModel.users.observeAsState()

    var isEditMode by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    //fetching user
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty() && user == null) {
            userViewModel.getUserById(currentUserId) { _, _ -> }
        }
    }

    //initialize
    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName
            lastName = it.lastName
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Settings",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF60A5FA), Color(0xFFA855F7))
                )
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (user == null) {
            Text(
                text = "Loading user info...",
                color = Color.White
            )
        } else {
            // Editable fields
            if (isEditMode) {
                // First Name
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    singleLine = true,
                    label = { Text("First Name") },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Last Name
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    singleLine = true,
                    label = { Text("Last Name") },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                UserInfoItem("First Name:", user?.firstName ?: "")
                UserInfoItem("Last Name:", user?.lastName ?: "")
            }

            UserInfoItem("Email:", user?.email ?: "")
            UserInfoItem("Date of Birth:", user?.dob ?: "")

            Spacer(modifier = Modifier.height(30.dp))

            //buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (isEditMode) {
                            // Save changes
                            val updatedUser = UserModel(
                                userId = currentUserId,
                                email = user?.email ?: "",
                                firstName = firstName,
                                lastName = lastName,
                                dob = user?.dob ?: "",
                                password = user?.password ?: ""
                            )
                            userViewModel.updateProfile(currentUserId, updatedUser) { _, _ -> }
                        }
                        isEditMode = !isEditMode
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isEditMode) "Save" else "Edit Profile")
                }

                if (isEditMode) {
                    Button(
                        onClick = {
                            // Cancel edits
                            firstName = user?.firstName ?: ""
                            lastName = user?.lastName ?: ""
                            isEditMode = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout button
            Button(
                onClick = {
                    userViewModel.repo.logout()
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Composable
fun UserInfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}
