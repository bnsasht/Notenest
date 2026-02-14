package com.example.project.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project.model.NoteModel
import com.example.project.viewmodel.NoteViewModel

@Composable
fun EditNoteDialog(
    viewModel: NoteViewModel,
    note: NoteModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0A0F19),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Edit Note",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2563EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Input
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2563EB)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (title.isBlank() || description.isBlank()) return@Button

                            isSaving = true
                            val updatedNote = note.copy(
                                title = title,
                                description = description
                            )

                            viewModel.updateNote(note.noteId, updatedNote) { success, msg ->
                                isSaving = false
                                if (success) {
                                    Toast.makeText(context, "Note updated!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text(if (isSaving) "Saving..." else "Save", color = Color.White)
                    }
                }
            }
        }
    }
}
