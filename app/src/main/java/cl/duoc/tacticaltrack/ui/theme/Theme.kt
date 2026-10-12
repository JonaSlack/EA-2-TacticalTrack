package cl.duoc.tacticaltrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0057B8),
    secondary = Color(0xFF006C7A),
    tertiary = Color(0xFF4B5F8A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFADC7FF),
    secondary = Color(0xFF52D7EE),
    tertiary = Color(0xFFB5C4FF)
)

@Composable
fun TacticalTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
