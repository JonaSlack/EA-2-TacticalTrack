package cl.duoc.tacticaltrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operatorName: String,
    val operatorRole: String,
    val pointId: String,
    val pointName: String,
    val assetId: String,
    val assetCode: String,
    val assetType: String,
    val visitType: String,
    val equipmentStatus: String,
    val observations: String,
    val photoUri: String,
    val latitude: Double,
    val longitude: Double,
    val locationAccuracyMeters: Float?,
    val distanceToPointMeters: Float,
    val presenceVerified: Boolean,
    val timestamp: Long,
    val supervisorStatus: String = "Pendiente",
    val synced: Boolean = false
)
