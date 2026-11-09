package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.BorderStroke
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

private val QzBackground = Color(0xFF0F141E)
private val QzGreen = Color(0xFF97CE4C)
private val QzBlue = Color(0xFF00B5CC)
private val QzCard = Color(0xFF1E2738)
private val QzYellow = Color(0xFFF0E14A)
private val QzAlive = Color(0xFF3DDC84)
private val QzDead = Color(0xFFE5484D)

@Composable
fun QuizScreen(
    character: RickCharacter?,
    lastAnswerCorrect: Boolean?,
    score: Int,
    best: Int,
    isNewRecord: Boolean,
    isGameOver: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onAnswer: (guessAlive: Boolean) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onRetry: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(containerColor = QzBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .border(1.dp, QzGreen, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = QzGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "¿Vivo o muerto?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = QzGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ScoreBox(label = "Racha", value = score, color = QzGreen, modifier = Modifier.weight(1f))
                ScoreBox(label = "Récord", value = best, color = QzYellow, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))

            when {
                character != null -> QuizRound(
                    character = character,
                    lastAnswerCorrect = lastAnswerCorrect,
                    score = score,
                    isNewRecord = isNewRecord,
                    isGameOver = isGameOver,
                    onAnswer = onAnswer,
                    onNext = onNext,
                    onRestart = onRestart,
                    onCharacterClick = onCharacterClick,
                    onBackClick = onBackClick
                )
                isLoading -> Box(Modifier.fillMaxWidth().padding(top = 100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = QzGreen)
                }
                else -> Column(
                    modifier = Modifier.padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage ?: "No se pudo cargar el personaje", color = Color.LightGray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) { Text("Reintentar", color = QzGreen) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScoreBox(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(QzCard, RoundedCornerShape(14.dp))
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp)
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 13.sp)
        Text(text = value.toString(), color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun QuizRound(
    character: RickCharacter,
    lastAnswerCorrect: Boolean?,
    score: Int,
    isNewRecord: Boolean,
    isGameOver: Boolean,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val answered = lastAnswerCorrect != null
    val isAlive = character.status.equals("alive", ignoreCase = true)
    val borderColor = when (lastAnswerCorrect) {
        null -> QzBlue
        true -> QzGreen
        false -> QzDead
    }

    AsyncImage(
        model = character.image,
        contentDescription = character.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(3.dp, borderColor, RoundedCornerShape(20.dp))
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = character.name,
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Text(text = character.species, color = QzBlue, fontSize = 15.sp)
    Spacer(modifier = Modifier.height(24.dp))

    if (!answered) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            AnswerButton("VIVO", QzAlive, Modifier.weight(1f)) { onAnswer(true) }
            AnswerButton("MUERTO", QzDead, Modifier.weight(1f)) { onAnswer(false) }
        }
        return
    }

    // Resultado de la respuesta
    Text(
        text = if (lastAnswerCorrect == true) "¡Correcto!" else "¡Incorrecto!",
        color = if (lastAnswerCorrect == true) QzGreen else QzDead,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = if (isAlive) "Está vivo" else "Está muerto",
        color = Color.LightGray,
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(20.dp))

    if (!isGameOver) {
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = QzGreen, contentColor = QzBackground),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("SIGUIENTE", fontWeight = FontWeight.Bold)
        }
        return
    }

    // Fin de la partida
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = QzCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isNewRecord) QzYellow else QzGreen, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Fin de la partida", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (score == 1) "Acertaste 1 seguido" else "Acertaste $score seguidos",
                color = Color.LightGray,
                fontSize = 15.sp
            )
            if (isNewRecord) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("¡Nuevo récord!", color = QzYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onRestart,
        colors = ButtonDefaults.buttonColors(containerColor = QzGreen, contentColor = QzBackground),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Text("JUGAR OTRA VEZ", fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(8.dp))
    TextButton(onClick = { onCharacterClick(character.id) }) {
        Text("Ver ficha de ${character.name}", color = QzBlue)
    }
    TextButton(onClick = onBackClick) {
        Text("Salir", color = Color.LightGray)
    }
}

@Composable
private fun AnswerButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(2.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = color.copy(alpha = 0.12f)),
        modifier = modifier.height(60.dp)
    ) {
        Text(text, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
