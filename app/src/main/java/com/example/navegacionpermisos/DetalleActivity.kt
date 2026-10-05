package com.example.navegacionpermisos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.navegacionpermisos.databinding.ActivityDetalleBinding

class DetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar ViewBinding
        binding = ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Agregar Nueva Activity (Código exacto de la Lámina 2 del PDF)
        val recibido = intent.getStringExtra(MainActivity.EXTRA_TEXTO).orEmpty()
        binding.txtDetalle.text = if (recibido.isNotEmpty()) {
            "Dato recibido: $recibido"
        } else {
            "No se recibió ningún dato"
        }

        // Botón para volver a la pantalla anterior
        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}
