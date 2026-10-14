package cl.duoc.tacticaltrack

import android.app.Application
import cl.duoc.tacticaltrack.data.local.AppDatabase
import cl.duoc.tacticaltrack.data.repository.UserPreferencesRepository
import cl.duoc.tacticaltrack.data.repository.VisitRepository

class TacticalTrackApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val visitRepository by lazy { VisitRepository(database.visitDao()) }
    val preferencesRepository by lazy { UserPreferencesRepository(this) }
}
