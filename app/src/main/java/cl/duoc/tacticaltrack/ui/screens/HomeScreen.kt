package cl.duoc.tacticaltrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.tacticaltrack.ui.components.StatCard
import cl.duoc.tacticaltrack.viewmodel.VisitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: VisitViewModel,
    isExpanded: Boolean,
    onNewVisit: () -> Unit
) {
    val visits by viewModel.visits.collectAsStateWithLifecycle()
    val operator by viewModel.operatorName.collectAsStateWithLifecycle()
    val role by viewModel.operatorRole.collectAsStateWithLifecycle()

    val pendingSync = visits.count { !it.synced }
    val verified = visits.count { it.presenceVerified }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TacticalTrack") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Hola, $operator · $role",
                style = MaterialTheme.typography.titleLarge
            )

            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("Visitas", visits.size.toString(), Modifier.weight(1f))
                    StatCard("GPS verificadas", verified.toString(), Modifier.weight(1f))
                    StatCard("Pendientes de sync", pendingSync.toString(), Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Visitas registradas", visits.size.toString(), Modifier.fillMaxWidth())
                    StatCard("GPS verificadas", verified.toString(), Modifier.fillMaxWidth())
                    StatCard("Pendientes de sincronización", pendingSync.toString(), Modifier.fillMaxWidth())
                }
            }

            AnimatedVisibility(
                visible = pendingSync > 0,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Modo offline activo",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Hay $pendingSync visita(s) almacenada(s) localmente. " +
                                "Para el MVP la sincronización con servidor se simula."
                        )
                    }
                }
            }

            Button(
                onClick = onNewVisit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = null)
                Text("  Registrar nueva visita")
            }

            OutlinedButton(
                onClick = viewModel::simulateSync,
                enabled = pendingSync > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Text("  Simular sincronización")
            }

            Text(
                "El registro real queda persistido con Room/SQLite. " +
                    "La API REST se deja como siguiente incremento del producto.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
