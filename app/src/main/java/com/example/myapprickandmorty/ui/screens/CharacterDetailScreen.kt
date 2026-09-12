package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapprickandmorty.data.model.RickCharacter

@Composable
fun CharacterDetailScreen(
    character: RickCharacter,
    onBackClick: () -> Unit
) {
    val backgroundColor = Color(0xFF0F141E)
    val rickGreenColor = Color(0xFF97CE4C)
    val cardColor = Color(0xFF1E2738)

    Scaffold(
        bottomBar = { CustomBottomNavigation() },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .border(1.dp, rickGreenColor, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = rickGreenColor
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Ficha de Sujeto",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = rickGreenColor
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(2.dp, rickGreenColor, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = character.image,
                    contentDescription = character.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFF0E14A), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ID: ${character.id}",
                        color = Color(0xFFF0E14A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, rickGreenColor, RoundedCornerShape(16.dp))
            ) {
                Column {
                    DetailRow(label = "Nombre", value = character.name)
                    HorizontalDivider(color = rickGreenColor.copy(alpha = 0.3f))
                    DetailRowStatus(label = "Estado", status = character.status)
                    HorizontalDivider(color = rickGreenColor.copy(alpha = 0.3f))
                    DetailRow(label = "Especie", value = character.species)
                    HorizontalDivider(color = rickGreenColor.copy(alpha = 0.3f))
                    DetailRow(label = "Tipo", value = "-")
                    HorizontalDivider(color = rickGreenColor.copy(alpha = 0.3f))
                    DetailRow(label = "Género", value = "Desconocido")
                    HorizontalDivider(color = rickGreenColor.copy(alpha = 0.3f))
                    DetailRow(label = "Origen", value = character.origin.name)
                    HorizontalDivider(color = rickGreenColor.copy(alpha = 0.3f))
                    DetailRow(label = "Locación", value = "Desconocida")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

@Composable
fun DetailRowStatus(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val statusColor = when (status.lowercase()) {
                "alive", "vivo" -> Color.Green
                "dead", "muerto" -> Color.Red
                else -> Color.Gray
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = status, color = statusColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}