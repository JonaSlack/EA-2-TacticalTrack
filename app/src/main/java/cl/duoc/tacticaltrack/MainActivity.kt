package cl.duoc.tacticaltrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import cl.duoc.tacticaltrack.navigation.TacticalTrackApp
import cl.duoc.tacticaltrack.ui.theme.TacticalTrackTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TacticalTrackTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
                TacticalTrackApp(isExpanded = isExpanded)
            }
        }
    }
}
