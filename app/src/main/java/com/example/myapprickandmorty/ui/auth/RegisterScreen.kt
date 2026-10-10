package com.example.myapprickandmorty.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .statusBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = RickGreen)
        Spacer(Modifier.height(32.dp))

        AuthTextField(name, { name = it; viewModel.clearError() }, "Nombre")
        Spacer(Modifier.height(12.dp))
        AuthTextField(email, { email = it; viewModel.clearError() }, "Correo", KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AuthTextField(password, { password = it; viewModel.clearError() }, "Contraseña (mín. 6)", KeyboardType.Password, isPassword = true)

        viewModel.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = Color(0xFFFF6B6B), fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.register(name, email, password, onRegistered) },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RickGreen),
            shape = RoundedCornerShape(30.dp)
        ) {
            if (viewModel.isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
            else Text("REGISTRARME", color = Color.Black, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("Ya tengo cuenta", color = TextBlue)
        }
    }
}
