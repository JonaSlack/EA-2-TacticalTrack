package cl.duoc.tacticaltrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.tacticaltrack.ui.components.VisitStatusChips
import cl.duoc.tacticaltrack.viewmodel.VisitViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitDetailScreen(
    visitId: Long,
    viewModel: VisitViewModel,
    onBack: () -> Unit
) {
    val flow = remember(visitId) { viewModel.observeVisit(visitId) }
    val visit by flow.collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de visita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        val current = visit
        if (current == null) {
            Text(
                "Cargando visita...",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            )
        } else {
            val date = SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss",
                Locale.getDefault()
            ).format(Date(current.timestamp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "${current.assetCode} · ${current.assetType}",
                    style = MaterialTheme.typography.headlineSmall
                )
                VisitStatusChips(
                    current.presenceVerified,
                    current.supervisorStatus,
                    current.synced
                )

                AsyncImage(
                    model = current.photoUri,
                    contentDescription = "Fotografía de evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("Punto: ${current.pointName}")
                        Text("Operario: ${current.operatorName} (${current.operatorRole})")
                        Text("Fecha y hora: $date")
                        Text("Tipo de visita: ${current.visitType}")
                        Text("Estado del equipo: ${current.equipmentStatus}")
                        Text("Observaciones: ${current.observations.ifBlank { "Sin observaciones" }}")
                        Text("Latitud: ${current.latitude}")
                        Text("Longitud: ${current.longitude}")
                        Text("Distancia al punto: ${"%.1f".format(current.distanceToPointMeters)} m")
                    }
                }
            }
        }
    }
}
