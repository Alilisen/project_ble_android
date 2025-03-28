package fr.isen.battault.androidsmartdevice.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BLEDevice(val name: String, val address: String, val rssi: Int)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    devices: List<BLEDevice>,
    isScanning: Boolean,
    remainingTime: Int,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onBack: () -> Unit,
    onDeviceClick: (BLEDevice) -> Unit
) {
    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Bluetooth") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9999),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isBluetoothEnabled) {
                Text(
                    text = "Le Bluetooth est désactivé. Veuillez l'activer.",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
                Button(
                    onClick = {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        context.startActivity(enableBtIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9999))
                ) {
                    Text("Activer Bluetooth")
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isScanning) "Scan en cours..." else "Appuyez sur lecture pour scanner",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = { if (isScanning) onStopScan() else onStartScan() }) {
                        Icon(
                            imageVector = if (isScanning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Start/Stop Scan",
                            tint = Color(0xFFFF9999),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Divider(modifier = Modifier.padding(bottom = 12.dp))

                if (devices.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Bluetooth,
                                contentDescription = "Bluetooth",
                                tint = Color(0xFFFF9999),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Aucun appareil détecté", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(devices) { device ->
                            BLEDeviceItem(device, onClick = { onDeviceClick(device) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BLEDeviceItem(device: BLEDevice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFFF9999),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${device.rssi} dB",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = device.name.ifEmpty { "Inconnu" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = device.address,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
