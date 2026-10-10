package com.example.myapprickandmorty.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

private val Bg = Color(0xFF0F141E)
private val Green = Color(0xFF97CE4C)
private val Blue = Color(0xFF00B5CC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // Photo Picker del sistema: no requiere permisos
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.onPhotoPicked(it) }
    }
    val profile = viewModel.profile

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = Green, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Green)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Green)
                return@Column
            }

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2738))
                        .border(3.dp, Blue, CircleShape)
                        .clickable(enabled = !viewModel.isUploading) {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!profile?.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = profile?.photoUrl,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Person, null, tint = Blue, modifier = Modifier.size(72.dp))
                    }
                    if (viewModel.isUploading) CircularProgressIndicator(color = Green)
                }
                Icon(
                    Icons.Default.CameraAlt, "Cambiar foto", tint = Color.Black,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Green).padding(8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(profile?.name?.ifBlank { "Sin nombre" } ?: "", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(profile?.email.orEmpty(), color = Blue, fontSize = 14.sp)

            viewModel.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = Color(0xFFFF6B6B), fontSize = 14.sp)
            }

            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = { viewModel.logout(); onLoggedOut() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(30.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Green)
            ) { Text("CERRAR SESIÓN", color = Green, fontWeight = FontWeight.Bold) }
        }
    }
}
