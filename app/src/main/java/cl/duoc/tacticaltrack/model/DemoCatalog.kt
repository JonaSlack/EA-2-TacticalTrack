package cl.duoc.tacticaltrack.model

/**
 * Datos 100% sintéticos para fines académicos.
 * Las coordenadas sirven para demostrar la comparación GPS del MVP.
 */
object DemoCatalog {

    val points = listOf(
        PointOfSale(
            id = "PV-001",
            name = "Punto Demo Central",
            address = "Sucursal académica 001",
            latitude = -33.5186,
            longitude = -70.7159
        ),
        PointOfSale(
            id = "PV-002",
            name = "Punto Demo Norte",
            address = "Sucursal académica 002",
            latitude = -33.4372,
            longitude = -70.6506
        ),
        PointOfSale(
            id = "PV-003",
            name = "Punto Demo Sur",
            address = "Sucursal académica 003",
            latitude = -33.5592,
            longitude = -70.6751
        )
    )

    val assets = listOf(
        CoolingAsset("ACT-001", "PV-001", "FRZ-001", "Vitrina freezer"),
        CoolingAsset("ACT-002", "PV-001", "CON-002", "Conservadora"),
        CoolingAsset("ACT-003", "PV-002", "FRZ-003", "Vitrina freezer"),
        CoolingAsset("ACT-004", "PV-002", "CON-004", "Conservadora"),
        CoolingAsset("ACT-005", "PV-003", "FRZ-005", "Vitrina freezer")
    )

    fun assetsForPoint(pointId: String): List<CoolingAsset> =
        assets.filter { it.pointId == pointId }

    fun pointById(id: String): PointOfSale? = points.firstOrNull { it.id == id }

    fun assetById(id: String): CoolingAsset? = assets.firstOrNull { it.id == id }
}
