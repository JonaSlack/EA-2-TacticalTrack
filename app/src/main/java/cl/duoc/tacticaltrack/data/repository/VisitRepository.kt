package cl.duoc.tacticaltrack.data.repository

import cl.duoc.tacticaltrack.data.local.VisitDao
import cl.duoc.tacticaltrack.data.local.VisitEntity
import kotlinx.coroutines.flow.Flow

class VisitRepository(
    private val visitDao: VisitDao
) {
    val visits: Flow<List<VisitEntity>> = visitDao.observeAll()

    fun observeVisit(id: Long): Flow<VisitEntity?> = visitDao.observeById(id)

    suspend fun saveVisit(visit: VisitEntity): Long = visitDao.insert(visit)

    suspend fun updateSupervisorStatus(id: Long, status: String) {
        visitDao.updateSupervisorStatus(id, status)
    }

    suspend fun simulateSync() {
        visitDao.markAllAsSynced()
    }
}
