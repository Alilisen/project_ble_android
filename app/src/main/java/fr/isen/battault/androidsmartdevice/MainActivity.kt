package fr.isen.battault.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.battault.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // Ajout d'une TopAppBar pour afficher le titre
            CenterAlignedTopAppBar(
                title = {
                    Text("Scanner Bluetooth")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF9999) // Couleur de la top bar
                )
            )
        },
        content = { paddingValues ->
            // Corps principal de l'écran centré
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Assurez-vous de prendre en compte les padding du Scaffold
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Centrer le contenu
            ) {
                // Image de l'icône (centrée)
                Image(
                    painter = painterResource(id = R.drawable.icon_ble_android), // Remplace avec ton icône
                    contentDescription = "Bluetooth Icon",
                    modifier = Modifier.size(128.dp)
                )

                Spacer(modifier = Modifier.height(32.dp)) // Espacement entre l'image et la description

                // Description de l'application
                Text(
                    text = "Cette application vous permet de scanner les appareils Bluetooth et de vous y connecter.",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp)) // Espacement entre la description et le bouton

                // Bouton pour lancer l'activité de scan
                Button(
                    onClick = {

                        navController.navigate("scan_screen")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9999) // Couleur personnalisée du bouton
                    )
                ) {
                    Text(text = "Démarrer le scan")
                }
            }
        }
    )
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                    AppNavigation()
                }
            }
        }
    }


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome_screen") {
        composable("welcome_screen") {
            WelcomeScreen(navController) // Screen de bienvenue
        }
        composable("scan_screen") {
            ScanScreen(navController) // Screen de scan
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidSmartDeviceTheme {
        Greeting("Android")
    }
}