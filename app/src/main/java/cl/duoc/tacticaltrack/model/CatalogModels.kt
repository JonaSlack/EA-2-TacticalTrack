package cl.duoc.tacticaltrack.model

data class PointOfSale(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class CoolingAsset(
    val id: String,
    val pointId: String,
    val code: String,
    val type: String
)
