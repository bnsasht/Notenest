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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.example.project.viewmodel.NoteViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FavouritesScreen(viewModel: NoteViewModel) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // fetching all notes
    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
    }

    // observing all notes
    val allNotes by viewModel.allNotes.observeAsState(initial = emptyList())

    // filtering favorites for each user
    val favNotes = allNotes?.filter { it.favorites[currentUserId] == true } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F19))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_favorite_24),
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Favourites",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (favNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No favourites yet",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(favNotes) { note ->
                    FavNoteCard(
                        title = note.title,
                        subtitle = "Shared by community",
                        onOpen = {
                            // Open PDF via PdfWebViewActivity
                            val intent = Intent(context, PdfWebViewActivity::class.java)
                            intent.putExtra("pdfUrl", note.pdfUrl)
                            context.startActivity(intent)

                        },
                        onUnfavorite = {
                            viewModel.toggleFavorite(note.noteId, currentUserId, false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavNoteCard(
    title: String,
    subtitle: String,
    onOpen: () -> Unit,
    onUnfavorite: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFD700).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_cloud_upload_24),
                        contentDescription = null,
                        tint = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            // Unfavorite Button
            IconButton(onClick = onUnfavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Unfavorite",
                    tint = Color(0xFFFFD700)
                )
            }
        }
    }
}
