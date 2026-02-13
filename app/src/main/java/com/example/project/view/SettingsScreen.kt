package com.example.project.view

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import com.example.project.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.project.view.LoginActivity

@Composable
fun SettingsScreen(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    val user by userViewModel.users.observeAsState()

    //fetching user data
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            userViewModel.getUserById(currentUserId) { _, _ -> }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Settings",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                brush = Brush.horizontalGradient(listOf(Color(0xFF60A5FA), Color(0xFFA855F7)))
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (user == null) {
            Text(
                text = "Loading user info...",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        } else {
            UserInfoItem(label = "First Name:", value = user?.firstName ?: "N/A")
            UserInfoItem(label = "Last Name:", value = user?.lastName ?: "N/A")
            UserInfoItem(label = "Email:", value = user?.email ?: "N/A")
            UserInfoItem(label = "Date of Birth:", value = user?.dob ?: "N/A")

            Spacer(modifier = Modifier.height(40.dp))


            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
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
