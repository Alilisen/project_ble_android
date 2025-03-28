package fr.isen.battault.androidsmartdevice.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    name: String,
    address: String,
    rssi: Int,
    onBack: () -> Unit,
    onConnectClick: () -> Unit,
    connectionStatus: String,
    isConnected: Boolean,
    ledStates: List<Boolean>,
    onLedToggle: (Int) -> Unit,
    isSubscribedButton1: Boolean,
    isSubscribedButton3: Boolean,
    onSubscribeToggleButton1: (Boolean) -> Unit,
    onSubscribeToggleButton3: (Boolean) -> Unit,
    counterButton1: Int,
    counterButton3: Int,
    onResetCounter: () -> Unit
) {
    val mainColor = Color(0xFFFF9999)

    val ledColors = listOf(
        Color(0xFF1976D2), // LED 1 - Bleu
        Color(0xFF4CAF50), // LED 2 - Vert
        Color(0xFFF44336)  // LED 3 - Rouge
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Android Smart Device") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainColor,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isConnected) {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE5E5)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Périphérique détecté", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = mainColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nom : $name", fontSize = 16.sp)
                        Text("Adresse : $address", fontSize = 14.sp, color = Color.Gray)
                        Text("RSSI : $rssi dBm", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("$connectionStatus", fontSize = 14.sp, color = Color(0xFFB71C1C))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onConnectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = mainColor)
                ) {
                    Text("Se connecter", color = Color.White, fontSize = 16.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selectionne ta lumière ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = mainColor)
                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(8.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ){

                    ledStates.forEachIndexed { index, isOn ->
                        val color = ledColors.getOrNull(index) ?: Color.Gray
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = { onLedToggle(index) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOn) color else Color.LightGray
                                ),
                                modifier = Modifier
                                    .height(64.dp)
                                    .width(100.dp)
                            ) {
                                Text(
                                    text = "LED ${index + 1}",
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                )
                {


                    Checkbox(
                        checked = isSubscribedButton3,
                        onCheckedChange = { onSubscribeToggleButton3(it) }
                    )
                    Text("Appuies pour activer les notifications du bouton 1")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSubscribedButton1,
                        onCheckedChange = { onSubscribeToggleButton1(it) }
                    )
                    Text("Appuies pour activer les notifications du bouton 3")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Compteur du bouton 1 : $counterButton3", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = mainColor)
                Text("Compteur du bouton 3 : $counterButton1", fontSize = 18.sp, fontWeight = FontWeight.Bold,color = mainColor)

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onResetCounter, colors = ButtonDefaults.buttonColors(containerColor = mainColor)) {
                    Text("Réinitialiser les compteurs", color = Color.White)
                }
            }
        }
    }
}