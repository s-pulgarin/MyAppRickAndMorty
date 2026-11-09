package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapprickandmorty.data.model.CharacterFilters
import com.example.myapprickandmorty.data.model.FilterOption
import com.example.myapprickandmorty.data.model.FilterOptions

private val SheetColor = Color(0xFF1E2738)
private val RickGreen = Color(0xFF97CE4C)
private val TextBlue = Color(0xFF00B5CC)
private val Background = Color(0xFF0F141E)

/** Hoja inferior con los grupos de filtros. Cada toque se aplica al instante. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterFilterSheet(
    filters: CharacterFilters,
    totalCount: Int?,
    onFiltersChange: (CharacterFilters) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SheetColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Filtros",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = RickGreen,
                    modifier = Modifier.weight(1f)
                )
                if (!filters.isEmpty) {
                    TextButton(onClick = { onFiltersChange(CharacterFilters()) }) {
                        Text("Limpiar", color = TextBlue)
                    }
                }
            }

            FilterGroup("Estado", FilterOptions.status, filters.status) {
                onFiltersChange(filters.copy(status = it))
            }
            FilterGroup("Género", FilterOptions.gender, filters.gender) {
                onFiltersChange(filters.copy(gender = it))
            }
            FilterGroup("Especie", FilterOptions.species, filters.species) {
                onFiltersChange(filters.copy(species = it))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RickGreen, contentColor = Background),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = when (totalCount) {
                        null -> "Ver resultados"
                        1 -> "Ver 1 resultado"
                        else -> "Ver $totalCount resultados"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/** Grupo de chips de selección única: tocar el chip ya elegido lo desmarca. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterGroup(
    title: String,
    options: List<FilterOption>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = title, color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option.value == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (isSelected) null else option.value) },
                label = { Text(option.label) },
                colors = filterChipColors(),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = TextBlue.copy(alpha = 0.6f),
                    selectedBorderColor = RickGreen
                )
            )
        }
    }
}

/** Fila con los filtros activos; cada chip se quita con su X. No se muestra si no hay filtros. */
@Composable
fun ActiveFiltersRow(
    filters: CharacterFilters,
    onFiltersChange: (CharacterFilters) -> Unit,
    modifier: Modifier = Modifier
) {
    if (filters.isEmpty) return
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        filters.status?.let { v ->
            ActiveChip(labelFor(FilterOptions.status, v)) { onFiltersChange(filters.copy(status = null)) }
        }
        filters.gender?.let { v ->
            ActiveChip(labelFor(FilterOptions.gender, v)) { onFiltersChange(filters.copy(gender = null)) }
        }
        filters.species?.let { v ->
            ActiveChip(labelFor(FilterOptions.species, v)) { onFiltersChange(filters.copy(species = null)) }
        }
        TextButton(onClick = { onFiltersChange(CharacterFilters()) }) {
            Text("Limpiar", color = TextBlue)
        }
    }
}

@Composable
private fun ActiveChip(label: String, onRemove: () -> Unit) {
    FilterChip(
        selected = true,
        onClick = onRemove,
        label = { Text(label) },
        trailingIcon = {
            Icon(Icons.Default.Close, contentDescription = "Quitar filtro $label", modifier = Modifier.size(16.dp))
        },
        colors = filterChipColors(),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = true,
            selectedBorderColor = RickGreen
        )
    )
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color.Transparent,
    labelColor = Color.White,
    selectedContainerColor = RickGreen.copy(alpha = 0.18f),
    selectedLabelColor = RickGreen,
    selectedLeadingIconColor = RickGreen,
    selectedTrailingIconColor = RickGreen
)

private fun labelFor(options: List<FilterOption>, value: String) =
    options.find { it.value == value }?.label ?: value
