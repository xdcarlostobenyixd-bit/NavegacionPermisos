package com.example.navegacionpermisos

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.navegacionpermisos.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    // ViewBinding según sintaxis del PDF (binding.xxx)
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val EXTRA_TEXTO = "EXTRA_TEXTO"
        private const val REQUEST_CODE = 100
    }

    // 4. Registrar Eventos del Ciclo de Vida (Lámina 3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialización de ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("CicloVida", "onCreate ejecutado")

        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        Log.d("CicloVida", "onStart ejecutado")
    }

    private fun setupListeners() {
        // 2. Agregar Nueva Activity (Lámina 2)
        binding.btnEnviar.setOnClickListener {
            val texto = binding.inputTexto.text.toString().ifBlank { "Sin texto" }
            val intent = Intent(this, DetalleActivity::class.java).apply {
                putExtra(EXTRA_TEXTO, texto)
            }
            startActivity(intent)
        }

        // 3. Solicitar Permiso Inalámbrico: Diálogo explicativo previo (Lámina 2)
        binding.btnSolicitarPermiso.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("¿Por qué pedimos este permiso?")
                .setMessage("Necesitamos acceso a ubicación para escanear dispositivos cercanos vía Bluetooth.")
                .setPositiveButton("Continuar") { _, _ ->
                    solicitarPermiso()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // 8. Mapear en el MainActivity.kt (Lámina 3)
        binding.btnListarBluetooth.setOnClickListener {
            listarDispositivosBluetooth()
        }

        binding.btnNombreWifi.setOnClickListener {
            mostrarNombreWifi()
        }
    }

    // 3. Función para solicitar permiso (Lámina 2)
    private fun solicitarPermiso() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    // 3. Manejo del resultado de la solicitud (Lámina 2)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Permiso denegado", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // 5. Mostrar dispositivos vinculados (Lámina 3)
    @SuppressLint("MissingPermission")
    private fun listarDispositivosBluetooth() {
        // Validación para Android 12+ (API 31+) donde se requiere BLUETOOTH_CONNECT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE)
                Toast.makeText(this, "Se requiere permiso Bluetooth Connect en Android 12+", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        val dispositivosVinculados = bluetoothAdapter.bondedDevices
        if (dispositivosVinculados.isNullOrEmpty()) {
            Log.d("Bluetooth", "No hay dispositivos vinculados o Bluetooth desactivado")
            Toast.makeText(this, "No hay dispositivos vinculados o Bluetooth desactivado", Toast.LENGTH_SHORT).show()
        } else {
            dispositivosVinculados.forEach { dispositivo ->
                Log.d("Bluetooth", "Nombre: ${dispositivo.name}, Dirección: ${dispositivo.address}")
            }
            Toast.makeText(this, "Dispositivos listados en Logcat (Filtro: Bluetooth)", Toast.LENGTH_SHORT).show()
        }
    }

    // 6. Obtener nombre de la red Wi-Fi (Lámina 3)
    private fun mostrarNombreWifi() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        val info = wifiManager.connectionInfo
        val nombreWifi = info.ssid

        Log.d("WiFi", "Conectado a: $nombreWifi")
        Toast.makeText(this, "Conectado a: $nombreWifi", Toast.LENGTH_SHORT).show()
    }
}