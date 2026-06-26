package net.harutiro.newalerm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import net.harutiro.newalerm.core.router.Router
import net.harutiro.newalerm.ui.theme.NewAlermTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewAlermTheme {
                Router()
            }
        }
    }
}
