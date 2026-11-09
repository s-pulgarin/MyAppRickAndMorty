package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
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
import coil.compose.AsyncImage
import com.example.myapprickandmorty.data.model.CharacterFilters
import com.example.myapprickandmorty.data.model.RickCharacter
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun HomeScreen(
    characterList: List<RickCharacter>,
    query: String,
    filters: CharacterFilters,
    totalCount: Int?,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    errorMessage: String?,
    onQueryChange: (String) -> Unit,
    onFiltersChange: (CharacterFilters) -> Unit,
    onLoadMore: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onEpisodesClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFF0F141E)
    val rickGreen = Color(0xFF97CE4C)
    val textBlue = Color(0xFF00B5CC)

    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var showFilters by rememberSaveable { mutableStateOf(false) }

    if (showFilters) {
        CharacterFilterSheet(
            filters = filters,
            totalCount = totalCount,
            onFiltersChange = onFiltersChange,
            onDismiss = { showFilters = false }
        )
    }

    // true cuando quedan pocos elementos por ver al final de la lista
    val shouldLoadMore by remember {
        derivedStateOf {
            val total = listState.layoutInfo.totalItemsCount
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 4
        }
    }
    LaunchedEffect(shouldLoadMore, characterList.size) {
        if (shouldLoadMore) onLoadMore()
    }
    // Al empezar una búsqueda nueva, volver al inicio de la lista
    LaunchedEffect(isLoading) {
        if (isLoading) listState.scrollToItem(0)
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selected = BottomTab.Characters,
                onCharactersClick = { scope.launch { listState.animateScrollToItem(0) } },
                onEpisodesClick = onEpisodesClick,
                onFavoritesClick = onFavoritesClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Dimensionados",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = rickGreen
            )
            Text(
                text = "Sujetos del Multiverso Registrados",
                fontSize = 14.sp,
                color = textBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar personaje...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textBlue) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = rickGreen,
                    unfocusedBorderColor = textBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = rickGreen
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Botón de filtros con un globo que indica cuántos hay activos
            BadgedBox(
                badge = {
                    if (!filters.isEmpty) {
                        Badge(containerColor = rickGreen, contentColor = backgroundColor) {
                            Text("${filters.activeCount}")
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = { focusManager.clearFocus(); showFilters = true },
                    modifier = Modifier
                        .size(56.dp)
                        .border(1.dp, if (filters.isEmpty) textBlue else rickGreen, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtros",
                        tint = if (filters.isEmpty) textBlue else rickGreen
                    )
                }
            }
            }

            ActiveFiltersRow(
                filters = filters,
                onFiltersChange = onFiltersChange,
                modifier = Modifier.padding(top = 10.dp)
            )
            if (!isLoading && totalCount != null && (query.isNotBlank() || !filters.isEmpty)) {
                Text(
                    text = if (totalCount == 1) "1 resultado" else "$totalCount resultados",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = rickGreen)
                }
                characterList.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = errorMessage ?: "No se encontraron personajes",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }
                else -> LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(characterList, key = { it.id }) { character ->
                        CharacterListCard(
                            character = character,
                            onClick = { onCharacterClick(character.id) }
                        )
                    }
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = rickGreen, modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterListCard(character: RickCharacter, onClick: () -> Unit) {
    val cardColor = Color(0xFF1E2738)
    val rickGreenColor = Color(0xFF97CE4C)
    val textBlueColor = Color(0xFF00B5CC)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, rickGreenColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, textBlueColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (character.status.lowercase()) {
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
                    Text(
                        text = "${character.status} • ${character.species}",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalle",
                tint = textBlueColor
            )
        }
    }
}
