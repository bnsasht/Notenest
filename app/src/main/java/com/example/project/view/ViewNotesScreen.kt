package com.example.project.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.example.project.model.NoteModel
import com.example.project.viewmodel.NoteViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

@Composable
fun ViewNotesScreen(viewModel: NoteViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteModel?>(null) }

    //fetching notes
    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
    }

    val allNotes by viewModel.allNotes.observeAsState(initial = emptyList())

    // Filter notes based on search query
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val filteredNotes = allNotes?.filter {
        it.userId == currentUserId && it.title.contains(searchQuery, ignoreCase = true)
    } ?: emptyList()

    //show edit dialog
    if (showEditDialog && noteToEdit != null) {
        EditNoteDialog(
            viewModel = viewModel,
            note = noteToEdit!!,
            onDismiss = {
                showEditDialog = false
                noteToEdit = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19))
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "My Note Library",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Access your uploaded documents",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by title...", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(painterResource(R.drawable.baseline_search_24), null, tint = Color(0xFF60A5FA)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (filteredNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notes found", color = Color.White.copy(alpha = 0.3f))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(filteredNotes) { note ->
                    NoteCardItem(
                        title = note.title,
                        description = note.description,

                        onOpen = {
                            Log.d("PDF_URL", note.pdfUrl)
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(note.pdfUrl), "application/pdf")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
                            }
                        },

                        onEdit = {
                            noteToEdit = note
                            showEditDialog = true
                        },
                        onDelete = {
                            viewModel.deleteNote(note.noteId) { success, msg ->
                                if (success) {
                                    Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteCardItem(
    title: String,
    description: String,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFFA855F7))),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.baseline_cloud_upload_24), null, tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onOpen) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Open", tint = Color(0xFF60A5FA))
                }
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF34D399))
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF4B2B))
                }
            }
        }
    }
}