package cl.duoc.tacticaltrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

    @Query("SELECT * FROM visits ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<VisitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: VisitEntity): Long

    @Query("UPDATE visits SET supervisorStatus = :status WHERE id = :id")
    suspend fun updateSupervisorStatus(id: Long, status: String)

    @Query("UPDATE visits SET synced = 1 WHERE synced = 0")
    suspend fun markAllAsSynced()
}
