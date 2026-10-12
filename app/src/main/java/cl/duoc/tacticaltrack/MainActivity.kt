package cl.duoc.tacticaltrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.tacticaltrack.ui.screens.HomeScreen
import cl.duoc.tacticaltrack.ui.theme.TacticalTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TacticalTrackTheme {
                HomeScreen()
            }
        }
    }
}
