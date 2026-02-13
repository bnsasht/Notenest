package com.example.project.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.repository.NoteRepoImplementation
import com.example.project.repository.UserRepoImplementation
import com.example.project.viewmodel.NoteViewModel
import com.example.project.viewmodel.UserViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val noteRepo = NoteRepoImplementation()
        val noteViewModel = NoteViewModel(noteRepo)

        enableEdgeToEdge()
        setContent {

            DashboardBody(noteViewModel)
        }
    }
}

data class NavItem(val label: String, val icon: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(viewModel: NoteViewModel) {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0A0F19),
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    NavItem("Upload", R.drawable.baseline_cloud_upload_24),
                    NavItem("View", R.drawable.baseline_visibility_24),
                    NavItem("Search", R.drawable.baseline_search_24),
                    NavItem("Favourites", R.drawable.baseline_favorite_24),
                    NavItem("Settings", R.drawable.baseline_settings_24)
                )
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                tint = if (selectedIndex == index) Color(0xFF60A5FA) else Color.Gray
                            )
                        },
                        label = { Text(item.label, color = Color.White) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            when (selectedIndex) {
                0 -> UploadScreen(viewModel)
                1 -> ViewNotesScreen(viewModel)
                2 -> SearchNotesScreen(viewModel)
                3 -> FavouritesScreen(viewModel)
                4 -> SettingsScreen(UserViewModel(UserRepoImplementation()))
            }
        }
    }
}