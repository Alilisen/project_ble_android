package fr.isen.battault.androidsmartdevice


import android.content.Context
import android.os.Bundle
import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import fr.isen.battault.androidsmartdevice.screens.BLEDevice
import fr.isen.battault.androidsmartdevice.screens.ScanScreen

class ScanActivity : ComponentActivity() {

    private val scannedDevices = mutableStateListOf<BLEDevice>()
    private var isScanning = mutableStateOf(false)
    private val remainingTime = mutableStateOf(0)
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var handler: Handler
    private lateinit var scanCallback: ScanCallback


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            launchScanUI()
        } else {
            Toast.makeText(this, "Attention, permissions refusées", Toast.LENGTH_LONG).show()
            finish()
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasPermissions(requiredPermissions())) {
            launchScanUI()
        } else {
            permissionLauncher.launch(requiredPermissions())
        }
    }
    @SuppressLint("SuspiciousIndentation")
    fun requiredPermissions(): Array<String> {
        val basePermissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) { // Android 12+
                listOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            }

            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) { // Android 10 & 11
                listOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            }

            else  { // Android < 10
                listOf(
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        return basePermissions.toTypedArray()
    }

    private fun hasPermissions(perms: Array<String>): Boolean {
        return perms.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

        private fun launchScanUI() {
            val bluetoothManager : BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth non disponible", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            if (!bluetoothAdapter.isEnabled) {
                Toast.makeText(this, "Veuillez activer le Bluetooth", Toast.LENGTH_LONG).show()
            }

            scanner = bluetoothAdapter.bluetoothLeScanner
            handler = Handler(Looper.getMainLooper())

            scanCallback = object : ScanCallback() {

                @SuppressLint("MissingPermission")
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    if (result != null && result.device.name != null) {
                        with(result.device) {
                            val indexQuery = scannedDevices.indexOfFirst { it.address == address }

                            if (indexQuery == -1) { // Device not found in the list, so we can add it
                                Log.i("SCANBLE_OK", "Device found: $name, Address: $address")
                                scannedDevices.add(BLEDevice(result.device.name, result.device.address, result.rssi))
                            }
                        }
                    }
                }

            }



            setContent {
                ScanScreen(
                    devices = scannedDevices,
                    isScanning = isScanning.value,
                    remainingTime = remainingTime.value,
                    onStartScan = { startScan() },
                    onStopScan = { stopScan() },
                    onBack = {
                        stopScan()
                        finish()
                    },
                    onDeviceClick = { device ->
                        stopScan()
                        val intent = Intent(this, DeviceActivity::class.java).apply {
                            putExtra("name", device.name)
                            putExtra("address", device.address)
                            putExtra("rssi", device.rssi)
                        }
                        startActivity(intent)
                    }
                )
            }
        }





            @SuppressLint("MissingPermission")
            private fun startScan() {
                if (isScanning.value) return
                scannedDevices.clear()
                remainingTime.value = 10
                isScanning.value = true

                scanner.startScan(scanCallback)
                Log.i("BLE", "Scan BLE démarré")

                Handler(Looper.getMainLooper()).post(object : Runnable {
                    override fun run() {

                        if (isScanning.value && remainingTime.value > 0) {
                            remainingTime.value -= 1
                            Handler(Looper.getMainLooper()).postDelayed(this, 1000)
                        }
                    }
                })

                handler.postDelayed({ stopScan() }, 10_000)
            }

            private fun stopScan() {
                if (!isScanning.value) return
                try {
                    scanner.stopScan(scanCallback)
                } catch (e: SecurityException) {
                    Log.e("BLE", "Erreur arrêt scan : ${e.message}")
                }
                isScanning.value = false
                remainingTime.value = 0
                handler.removeCallbacksAndMessages(null)
                Log.i("BLE", "Scan BLE arrêté")
            }
        }


















