package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapprickandmorty.R

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    val backgroundColor = Color(0xFF0F141E)
    val rickGreenColor = Color(0xFF97CE4C)
    val textBlueColor = Color(0xFF00B5CC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_rickandmorty),
            contentDescription = "Rick and Morty Logo",
            modifier = Modifier.size(280.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "¡Wubba Lubba Dub Dub!",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = rickGreenColor,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bienvenido al Buscador Multidimensional",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textBlueColor,
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onNavigateToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = rickGreenColor),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "CRUZAR PORTAL",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Icono de flecha",
                tint = Color.Black
            )
        }
    }
}