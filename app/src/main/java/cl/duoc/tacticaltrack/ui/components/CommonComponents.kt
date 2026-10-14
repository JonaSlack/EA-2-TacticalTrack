package cl.duoc.tacticaltrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectorField(
    label: String,
    selected: String,
    options: List<Pair<String, String>>,
    error: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember(enabled, options) { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled && options.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selected.ifBlank { "Seleccionar" },
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir opciones")
            }
            DropdownMenu(
                expanded = expanded && enabled && options.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentWidth()
            ) {
                options.forEach { (value, text) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelected(value)
                            expanded = false
                        }
                    )
                }
            }
        }
        if (error != null) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun VisitStatusChips(
    verified: Boolean,
    supervisorStatus: String,
    synced: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = {},
            label = { Text(if (verified) "GPS verificado" else "GPS fuera de radio") }
        )
        AssistChip(
            onClick = {},
            label = { Text(supervisorStatus) }
        )
        AssistChip(
            onClick = {},
            label = { Text(if (synced) "Sincronizada" else "Local") }
        )
    }
}
