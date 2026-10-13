package cl.duoc.tacticaltrack.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.tacticaltrack.data.local.VisitEntity
import cl.duoc.tacticaltrack.data.repository.VisitRepository
import cl.duoc.tacticaltrack.model.CoolingAsset
import cl.duoc.tacticaltrack.model.DemoCatalog
import cl.duoc.tacticaltrack.model.PointOfSale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VisitErrors(
    val point: String? = null,
    val asset: String? = null,
    val visitType: String? = null,
    val equipmentStatus: String? = null,
    val observations: String? = null,
    val photo: String? = null,
    val location: String? = null
)

data class VisitFormUiState(
    val pointId: String = "",
    val assetId: String = "",
    val visitType: String = "",
    val equipmentStatus: String = "",
    val observations: String = "",
    val photoUri: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAccuracyMeters: Float? = null,
    val distanceToPointMeters: Float? = null,
    val presenceVerified: Boolean = false,
    val isLocating: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val locationMessage: String = "",
    val errors: VisitErrors = VisitErrors()
)

class VisitViewModel(
    private val repository: VisitRepository
) : ViewModel() {

    companion object {
        // Regla PROPUESTA para el MVP académico; debe validarse con el dueño.
        const val PRESENCE_RADIUS_METERS = 150f
    }

    val points: List<PointOfSale> = DemoCatalog.points
    val visitTypes = listOf("Mantenimiento", "Venta", "Transporte")
    val equipmentStatuses = listOf("Operativo", "Operativo con observaciones", "Con falla")

    private val _form = MutableStateFlow(VisitFormUiState())
    val form: StateFlow<VisitFormUiState> = _form.asStateFlow()

    private val _operatorName = MutableStateFlow("Usuario Demo")
    val operatorName: StateFlow<String> = _operatorName.asStateFlow()

    private val _operatorRole = MutableStateFlow("Operario")
    val operatorRole: StateFlow<String> = _operatorRole.asStateFlow()

    val visits: StateFlow<List<VisitEntity>> = repository.visits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setOperator(name: String, role: String) {
        _operatorName.value = name
        _operatorRole.value = role
    }

    fun assetsForSelectedPoint(): List<CoolingAsset> =
        DemoCatalog.assetsForPoint(_form.value.pointId)

    fun onPointSelected(pointId: String) {
        _form.update {
            it.copy(
                pointId = pointId,
                assetId = "",
                errors = it.errors.copy(point = null, asset = null)
            )
        }
    }

    fun onAssetSelected(assetId: String) {
        _form.update { it.copy(assetId = assetId, errors = it.errors.copy(asset = null)) }
    }

    fun onVisitTypeSelected(value: String) {
        _form.update { it.copy(visitType = value, errors = it.errors.copy(visitType = null)) }
    }

    fun onEquipmentStatusSelected(value: String) {
        _form.update {
            it.copy(
                equipmentStatus = value,
                errors = it.errors.copy(equipmentStatus = null, observations = null)
            )
        }
    }

    fun onObservationsChange(value: String) {
        _form.update { it.copy(observations = value, errors = it.errors.copy(observations = null)) }
    }

    fun setPhoto(uri: String) {
        _form.update { it.copy(photoUri = uri, errors = it.errors.copy(photo = null)) }
    }

    fun setLocating(value: Boolean) {
        _form.update { it.copy(isLocating = value) }
    }

    fun setLocationError(message: String) {
        _form.update {
            it.copy(
                isLocating = false,
                locationMessage = message,
                errors = it.errors.copy(location = message)
            )
        }
    }

    fun setLocation(location: Location) {
        val state = _form.value
        val point = DemoCatalog.pointById(state.pointId)

        if (point == null) {
            setLocationError("Selecciona un punto de venta antes de registrar la ubicación.")
            return
        }

        val results = FloatArray(1)
        Location.distanceBetween(
            location.latitude,
            location.longitude,
            point.latitude,
            point.longitude,
            results
        )
        val distance = results[0]
        val verified = distance <= PRESENCE_RADIUS_METERS

        _form.update {
            it.copy(
                latitude = location.latitude,
                longitude = location.longitude,
                locationAccuracyMeters = location.accuracy,
                distanceToPointMeters = distance,
                presenceVerified = verified,
                isLocating = false,
                locationMessage = if (verified) {
                    "Presencia compatible con el punto de venta."
                } else {
                    "Ubicación registrada fuera del radio de ${PRESENCE_RADIUS_METERS.toInt()} m."
                },
                errors = it.errors.copy(location = null)
            )
        }
    }

    fun validateBeforeEvidence(): Boolean {
        val s = _form.value
        val errors = VisitErrors(
            point = if (s.pointId.isBlank()) "Selecciona un punto de venta." else null,
            asset = if (s.assetId.isBlank()) "Selecciona un activo." else null,
            visitType = if (s.visitType.isBlank()) "Selecciona el tipo de visita." else null,
            equipmentStatus = if (s.equipmentStatus.isBlank()) "Selecciona el estado del equipo." else null,
            observations = if (
                s.equipmentStatus.isNotBlank() && s.equipmentStatus != "Operativo" && s.observations.trim().length < 5
            ) "Describe la observación o falla con al menos 5 caracteres." else null
        )
        _form.update { it.copy(errors = errors) }
        return listOf(
            errors.point,
            errors.asset,
            errors.visitType,
            errors.equipmentStatus,
            errors.observations
        ).all { it == null }
    }

    private fun validateEvidence(): Boolean {
        val s = _form.value
        val errors = s.errors.copy(
            photo = if (s.photoUri.isBlank()) "Debes adjuntar evidencia fotográfica." else null,
            location = if (s.latitude == null || s.longitude == null) {
                "Debes registrar la ubicación del dispositivo."
            } else null
        )
        _form.update { it.copy(errors = errors) }
        return errors.photo == null && errors.location == null
    }

    fun saveVisit(onSaved: (Long) -> Unit) {
        if (!validateBeforeEvidence() || !validateEvidence()) return

        val s = _form.value
        val point = DemoCatalog.pointById(s.pointId) ?: return
        val asset = DemoCatalog.assetById(s.assetId) ?: return

        _form.update { it.copy(isSaving = true, saveSuccess = false) }

        viewModelScope.launch {
            val id = repository.saveVisit(
                VisitEntity(
                    operatorName = _operatorName.value,
                    operatorRole = _operatorRole.value,
                    pointId = point.id,
                    pointName = point.name,
                    assetId = asset.id,
                    assetCode = asset.code,
                    assetType = asset.type,
                    visitType = s.visitType,
                    equipmentStatus = s.equipmentStatus,
                    observations = s.observations.trim(),
                    photoUri = s.photoUri,
                    latitude = s.latitude!!,
                    longitude = s.longitude!!,
                    locationAccuracyMeters = s.locationAccuracyMeters,
                    distanceToPointMeters = s.distanceToPointMeters ?: Float.MAX_VALUE,
                    presenceVerified = s.presenceVerified,
                    timestamp = System.currentTimeMillis()
                )
            )
            _form.update { it.copy(isSaving = false, saveSuccess = true) }
            onSaved(id)
        }
    }

    fun clearForm() {
        _form.value = VisitFormUiState()
    }

    fun observeVisit(id: Long) = repository.observeVisit(id)

    fun approveVisit(id: Long) {
        viewModelScope.launch {
            repository.updateSupervisorStatus(id, "Aprobada")
        }
    }

    fun rejectVisit(id: Long) {
        viewModelScope.launch {
            repository.updateSupervisorStatus(id, "Rechazada")
        }
    }

    fun simulateSync() {
        viewModelScope.launch {
            repository.simulateSync()
        }
    }

    class Factory(
        private val repository: VisitRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VisitViewModel(repository) as T
        }
    }
}
