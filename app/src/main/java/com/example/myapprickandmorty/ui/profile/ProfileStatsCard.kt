package com.example.myapprickandmorty.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapprickandmorty.data.favorites.FavoriteStats

private val StGreen = Color(0xFF97CE4C)
private val StBlue = Color(0xFF00B5CC)
private val StCard = Color(0xFF1E2738)
private val StAlive = Color(0xFF3DDC84)
private val StDead = Color(0xFFE5484D)
private val StUnknown = Color(0xFF8A94A6)

/** Tarjeta "Tus estadísticas" calculada a partir de los favoritos. */
@Composable
fun ProfileStatsCard(stats: FavoriteStats) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StBlue, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tus estadísticas", color = StGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            if (stats.total == 0) {
                Text(
                    text = "Agrega personajes a favoritos para ver aquí tus estadísticas.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                return@Column
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatBox("Favoritos", stats.total, StGreen, Modifier.weight(1f))
                StatBox("Vivos", stats.alive, StAlive, Modifier.weight(1f))
                StatBox("Muertos", stats.dead, StDead, Modifier.weight(1f))
            }

            // Barra con la proporción vivos / muertos / desconocidos
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF0F141E))
            ) {
                if (stats.alive > 0) Box(Modifier.weight(stats.alive.toFloat()).fillMaxHeight().background(StAlive))
                if (stats.dead > 0) Box(Modifier.weight(stats.dead.toFloat()).fillMaxHeight().background(StDead))
                if (stats.unknown > 0) Box(Modifier.weight(stats.unknown.toFloat()).fillMaxHeight().background(StUnknown))
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Legend("Vivos", StAlive)
                Legend("Muertos", StDead)
                if (stats.unknown > 0) Legend("Desconocido (${stats.unknown})", StUnknown)
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = StGreen.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))
            Text("Especies favoritas", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            stats.topSpecies.forEachIndexed { index, (species, count) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}.",
                        color = if (index == 0) StGreen else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = if (species.equals("unknown", true)) "Desconocida" else species,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (count == 1) "1 personaje" else "$count personajes",
                        color = StBlue,
                        fontSize = 14.sp
                    )
                }
            }

            stats.lastAdded?.let {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = StGreen.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Último agregado", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = it.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f).padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: Int, color: Color, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color(0xFF0F141E), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp)
    ) {
        Text(value.toString(), color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.LightGray, fontSize = 12.sp)
    }
}

@Composable
private fun Legend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(5.dp))
        Text(label, color = Color.LightGray, fontSize = 12.sp)
    }
}
