package cl.duoc.tacticaltrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.tacticaltrack.data.local.VisitEntity
import cl.duoc.tacticaltrack.ui.components.VisitStatusChips
import cl.duoc.tacticaltrack.viewmodel.VisitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: VisitViewModel,
    onVisitClick: (Long) -> Unit
) {
    val visits by viewModel.visits.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Historial de visitas") })
        }
    ) { innerPadding ->
        if (visits.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Aún no hay visitas registradas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visits, key = { it.id }) { visit ->
                    VisitHistoryCard(visit, onVisitClick)
                }
            }
        }
    }
}

@Composable
private fun VisitHistoryCard(
    visit: VisitEntity,
    onVisitClick: (Long) -> Unit
) {
    val date = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    ).format(Date(visit.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVisitClick(visit.id) }
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "${visit.assetCode} · ${visit.assetType}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(visit.pointName)
            Text("${visit.visitType} · $date")
            Text("Estado equipo: ${visit.equipmentStatus}")
            VisitStatusChips(
                verified = visit.presenceVerified,
                supervisorStatus = visit.supervisorStatus,
                synced = visit.synced
            )
        }
    }
}
