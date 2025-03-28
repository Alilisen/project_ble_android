package fr.isen.battault.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.isen.battault.androidsmartdevice.screens.MainScreen
import fr.isen.battault.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                MainScreen(::onButtonClick)
            }
        }
    }

    private fun onButtonClick() {
        // Démarrer l'activité de scan
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}



