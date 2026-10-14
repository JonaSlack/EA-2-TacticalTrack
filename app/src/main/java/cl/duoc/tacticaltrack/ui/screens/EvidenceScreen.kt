package cl.duoc.tacticaltrack.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.tacticaltrack.util.copyImageToInternalStorage
import cl.duoc.tacticaltrack.util.createVisitImageUri
import cl.duoc.tacticaltrack.util.hasLocationPermission
import cl.duoc.tacticaltrack.util.requestCurrentLocation
import cl.duoc.tacticaltrack.viewmodel.VisitViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceScreen(
    viewModel: VisitViewModel,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.form.collectAsStateWithLifecycle()
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let { viewModel.setPhoto(it.toString()) }
        }
    }

    fun openCamera() {
        val uri = createVisitImageUri(context)
        pendingCameraUri = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) openCamera()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            copyImageToInternalStorage(context, it)?.let { localUri ->
                viewModel.setPhoto(localUri.toString())
            }
        }
    }

    fun locate() {
        viewModel.setLocating(true)
        requestCurrentLocation(
            context = context,
            onSuccess = viewModel::setLocation,
            onError = viewModel::setLocationError
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) locate()
        else viewModel.setLocationError("Permiso de ubicación rechazado.")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evidencia de visita") },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("2. Evidencia fotográfica", style = MaterialTheme.typography.titleLarge)

            if (state.photoUri.isNotBlank()) {
                AsyncImage(
                    model = state.photoUri,
                    contentDescription = "Evidencia de la visita",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Button(
                onClick = {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) openCamera()
                    else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Text("  Tomar fotografía")
            }

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Text("  Elegir desde galería")
            }

            state.errors.photo?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Text("3. Verificación de ubicación", style = MaterialTheme.typography.titleLarge)

            OutlinedButton(
                onClick = {
                    if (hasLocationPermission(context)) {
                        locate()
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = !state.isLocating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                    Text("  Obteniendo ubicación...")
                } else {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Text("  Registrar ubicación GPS")
                }
            }

            if (state.latitude != null && state.longitude != null) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Latitud: ${"%.6f".format(state.latitude)}")
                        Text("Longitud: ${"%.6f".format(state.longitude)}")
                        state.locationAccuracyMeters?.let {
                            Text("Precisión aproximada: ${"%.1f".format(it)} m")
                        }
                        state.distanceToPointMeters?.let {
                            Text("Distancia al punto: ${"%.1f".format(it)} m")
                        }
                        Text(
                            if (state.presenceVerified) {
                                "Resultado: presencia compatible"
                            } else {
                                "Resultado: fuera del radio configurado"
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            if (state.locationMessage.isNotBlank()) {
                Text(
                    state.locationMessage,
                    color = if (state.presenceVerified) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            state.errors.location?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            AnimatedVisibility(
                visible = state.saveSuccess,
                enter = fadeIn()
            ) {
                Text("Visita guardada correctamente en Room.")
            }

            Button(
                onClick = { viewModel.saveVisit(onSaved) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                    Text("  Guardando...")
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Text("  Guardar visita")
                }
            }

            Text(
                "La validación por radio GPS es una regla propuesta del MVP académico " +
                    "y debe confirmarse con el dueño antes de una implementación productiva.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
