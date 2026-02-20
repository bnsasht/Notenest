package com.example.project.view

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.viewmodel.NoteViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SearchNotesScreen(viewModel: NoteViewModel) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // fetching all notes
    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
    }

    val allNotes by viewModel.allNotes.observeAsState(initial = emptyList())

    // showing notes that match the title
    val filteredNotes = allNotes?.filter {
        it.title.contains(query, ignoreCase = true)
    } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19))
            .padding(24.dp)
    ) {
        Text("Deep Search", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Find notes from the entire community", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search title ...", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF60A5FA)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.07f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.07f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SearchTag("PDFs")
            SearchTag("Community")
            SearchTag("Shared")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            query.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Start typing to find notes...", color = Color.White.copy(alpha = 0.3f))
                }
            }
            filteredNotes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notes found for '$query'", color = Color.White.copy(alpha = 0.3f))
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredNotes) { note ->
                        val isFav = note.favorites.containsKey(currentUserId)

                        SearchNoteCard(
                            title = note.title,
                            isFavorite = isFav,
                            onOpen = {
                                // Open PDF inside PdfWebViewActivity
                                val intent = Intent(context, PdfWebViewActivity::class.java).apply {
                                    putExtra("pdfUrl", note.pdfUrl)
                                    putExtra("title", note.title)
                                }
                                context.startActivity(intent)
                            },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(note.noteId, currentUserId, !isFav)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchNoteCard(
    title: String,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Click to view PDF", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFF4B2B) else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SearchTag(label: String) {
    Surface(
        color = Color(0xFF2563EB).copy(alpha = 0.15f),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.3f))
    ) {
        Text(
            label,
            color = Color(0xFF60A5FA),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 12.sp
        )
    }
}
