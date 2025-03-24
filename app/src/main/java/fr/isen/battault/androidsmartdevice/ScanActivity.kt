package fr.isen.battault.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.battault.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import android.Manifest
import android.os.Build

class ScanActivity : ComponentActivity() {

    // Vérification des permissions Bluetooth et localisation
    private fun hasPermissions(): Boolean {
        return checkBluetoothPermissions(this)
    }

    private fun requestPermissions() {
        // Demander les permissions adaptées à la version Android
        requestPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vérifier si les permissions sont accordées
        if (!hasPermissions()) {
            requestPermissions()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavHostController) {
    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    // States
    var isScanning by remember { mutableStateOf(false) }
    var isBluetoothAvailable by remember { mutableStateOf(true) }
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    val devices = remember { mutableStateListOf<String>() }
    var permissionsGranted by remember { mutableStateOf(false) }

    // Vérification de la disponibilité du Bluetooth
    LaunchedEffect(Unit) {
        if (bluetoothAdapter == null) {
            isBluetoothAvailable = false
        } else {
            // Vérifier et demander les permissions nécessaires
            permissionsGranted = checkBluetoothPermissions(context)
            if (!permissionsGranted) {
                requestPermissions(context)
            }
        }
    }

    // Gérer les interactions avec le bouton de démarrage/arrêt du scan
    LaunchedEffect(isScanning) {
        if (isScanning && permissionsGranted) {
            // Simuler l'ajout d'appareils toutes les 2 secondes
            devices.clear()
            devices.add("Appareil 1")
            devices.add("Appareil 2")
            devices.add("Appareil 3")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan Bluetooth") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF9999)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Message d'information selon le statut du Bluetooth
                when {
                    !isBluetoothAvailable -> {
                        Text(
                            text = "Bluetooth n'est pas disponible sur ce smartphone.",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                    }
                    !isBluetoothEnabled -> {
                        Text(
                            text = "Le Bluetooth est désactivé. Veuillez l'activer.",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                        // Demander à l'utilisateur d'activer le Bluetooth
                        Button(
                            onClick = {
                                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                context.startActivity(enableBtIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9999))
                        ) {
                            Text("Activer Bluetooth")
                        }
                    }
                    !permissionsGranted -> {
                        Text(
                            text = "Les permissions Bluetooth et/ou localisation sont nécessaires.",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                    }
                    else -> {
                        // Si Bluetooth est activé et les permissions sont accordées
                        Text(
                            text = if (isScanning) "Recherche des appareils Bluetooth..." else "Le scan est arrêté.",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Liste d'appareils détectés
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(devices.size) { index ->
                        Text(text = devices[index])
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bouton pour démarrer/arrêter le scan
                if (isBluetoothAvailable && isBluetoothEnabled && permissionsGranted) {
                    Button(
                        onClick = {
                            isScanning = !isScanning
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9999))
                    ) {
                        Text(text = if (isScanning) "Arrêter le scan" else "Démarrer le scan")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bouton pour revenir à l'accueil
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9999))
                ) {
                    Text("Retour à l'accueil")
                }
            }
        }
    )
}

fun checkBluetoothPermissions(context: Context): Boolean {
    val bluetoothScanPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
    val bluetoothConnectPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
    val locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // Android 12 et versions ultérieures
            bluetoothScanPermission == PackageManager.PERMISSION_GRANTED &&
                    bluetoothConnectPermission == PackageManager.PERMISSION_GRANTED
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            // Android 10 et Android 11 (API 29 et 30)
            locationPermission == PackageManager.PERMISSION_GRANTED
        }
        else -> {
            // Versions Android inférieures à Q
            locationPermission == PackageManager.PERMISSION_GRANTED
        }
    }
}

fun requestPermissions(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12 et versions ultérieures
        ActivityCompat.requestPermissions(
            context as ComponentActivity,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            ),
            1
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10 et Android 11
        ActivityCompat.requestPermissions(
            context as ComponentActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    } else {
        // Anciennes versions Android
        ActivityCompat.requestPermissions(
            context as ComponentActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    }
}


