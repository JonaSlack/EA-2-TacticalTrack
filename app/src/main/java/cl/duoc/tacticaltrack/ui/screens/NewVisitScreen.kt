package cl.duoc.tacticaltrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.tacticaltrack.model.DemoCatalog
import cl.duoc.tacticaltrack.ui.components.SelectorField
import cl.duoc.tacticaltrack.viewmodel.VisitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewVisitScreen(
    viewModel: VisitViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val state by viewModel.form.collectAsStateWithLifecycle()
    val assets = DemoCatalog.assetsForPoint(state.pointId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva visita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "1. Datos de la revisión",
                style = MaterialTheme.typography.titleLarge
            )

            SelectorField(
                label = "Punto de venta",
                selected = DemoCatalog.pointById(state.pointId)?.name.orEmpty(),
                options = viewModel.points.map { it.id to it.name },
                error = state.errors.point,
                onSelected = viewModel::onPointSelected
            )

            SelectorField(
                label = "Activo",
                selected = DemoCatalog.assetById(state.assetId)?.let {
                    "${it.code} · ${it.type}"
                }.orEmpty(),
                options = assets.map { it.id to "${it.code} · ${it.type}" },
                error = state.errors.asset,
                onSelected = viewModel::onAssetSelected,
                enabled = assets.isNotEmpty()
            )

            SelectorField(
                label = "Tipo de visita",
                selected = state.visitType,
                options = viewModel.visitTypes.map { it to it },
                error = state.errors.visitType,
                onSelected = viewModel::onVisitTypeSelected
            )

            SelectorField(
                label = "Estado del equipo",
                selected = state.equipmentStatus,
                options = viewModel.equipmentStatuses.map { it to it },
                error = state.errors.equipmentStatus,
                onSelected = viewModel::onEquipmentStatusSelected
            )

            OutlinedTextField(
                value = state.observations,
                onValueChange = viewModel::onObservationsChange,
                label = { Text("Observaciones") },
                minLines = 3,
                isError = state.errors.observations != null,
                trailingIcon = {
                    if (state.errors.observations != null) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = "Error de observaciones")
                    }
                },
                supportingText = {
                    state.errors.observations?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (viewModel.validateBeforeEvidence()) {
                        onContinue()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar a evidencia")
            }
        }
    }
}
