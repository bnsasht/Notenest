package com.example.project.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.example.project.R
import com.example.project.model.NoteModel
import com.example.project.viewmodel.NoteViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun UploadScreen(viewModel: NoteViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("No file selected") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    //launcher to pick pdf file
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            fileName = "PDF Selected"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        NoteInput(value = title, onValueChange = { title = it }, label = "Note Title", placeholder = "e.g. Quantum Mechanics")
        Spacer(modifier = Modifier.height(20.dp))
        NoteInput(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Briefly explain what's in the PDF...", isLarge = true)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Attachment",
            modifier = Modifier.align(Alignment.Start),
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // this triggers file picker
        FilePickerBox(fileName = fileName) {
            pdfPickerLauncher.launch("application/pdf")
        }

        Spacer(modifier = Modifier.height(40.dp))

        // this shows loading indicator on the button
        if (isUploading) {
            CircularProgressIndicator(color = Color(0xFF60A5FA))
        } else {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty() && selectedFileUri != null) {
                        isUploading = true

                        // uploads the pdf
                        viewModel.uploadPdfToStorage(selectedFileUri!!) { success, downloadUrl ->
                            if (success) {
                                val noteId = UUID.randomUUID().toString()
                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                                val newNote = NoteModel(
                                    noteId = noteId,
                                    userId = currentUserId,
                                    title = title,
                                    description = description,
                                    pdfUrl = downloadUrl,
                                    timestamp = System.currentTimeMillis()
                                )

                                // saves note in database
                                viewModel.addNote(noteId, newNote) { dbSuccess, message ->
                                    isUploading = false
                                    if (dbSuccess) {
                                        Toast.makeText(context, "Note Published!", Toast.LENGTH_SHORT).show()
                                        // then reset fields
                                        title = ""
                                        description = ""
                                        fileName = "No file selected"
                                        selectedFileUri = null
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                isUploading = false
                                Toast.makeText(context, "Upload failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields and select a PDF", Toast.LENGTH_SHORT).show()
                    }
                },
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
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_cloud_upload_24),
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
fun NoteInput(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, isLarge: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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