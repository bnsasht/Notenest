package com.example.project.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import com.example.project.R


@Composable
fun UploadScreen() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("No file selected") } // Track PDF name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19)) // Deepest dark blue
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Allow scrolling for smaller phones
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Create Note",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                brush = Brush.horizontalGradient(listOf(Color(0xFF60A5FA), Color(0xFFA855F7)))
            )
        )
        Text(
            text = "Share your knowledge with the world",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Inputs
        NoteInput(value = title, onValueChange = { title = it }, label = "Note Title", placeholder = "e.g. Quantum Mechanics")
        Spacer(modifier = Modifier.height(20.dp))
        NoteInput(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Briefly explain what's in the PDF...", isLarge = true)

        Spacer(modifier = Modifier.height(24.dp))

        // THE DOTTED PDF BOX (The "Star" of the UI)
        Text(
            text = "Attachment",
            modifier = Modifier.align(Alignment.Start),
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        FilePickerBox(fileName = fileName) {
            // This is where your file picker intent will go later
            fileName = "Physics_Lecture_1.pdf"
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Publish Button with Glow
        Button(
            onClick = { /* Firebase Upload Logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Icon(painterResource(R.drawable.baseline_cloud_upload_24), contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Publish Note", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilePickerBox(fileName: String, onClick: () -> Unit) {
    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFF2563EB).copy(alpha = 0.5f),
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(16.dp))
            .clickable() { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_cloud_upload_24), // Use a PDF or Upload icon
                contentDescription = null,
                tint = if (fileName == "No file selected") Color.White.copy(alpha = 0.4f) else Color(0xFF60A5FA),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = fileName,
                color = if (fileName == "No file selected") Color.White.copy(alpha = 0.4f) else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NoteInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isLarge: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLarge) 150.dp else 56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF2563EB)
            )
        )
    }
}