package com.proyectomovil.snorb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.proyectomovil.snorb.databinding.ActivityCrearAnuncioBinding

class CrearAnuncio : AppCompatActivity() {
    private lateinit var binding: ActivityCrearAnuncioBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityCrearAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_anuncio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Listener para el botón de publicación
        binding.btnPublicar.setOnClickListener {
            val titulo = binding.tituloText.text.toString().trim()
            val contenido = binding.textoContenido.text.toString().trim()

            if (titulo.isEmpty() || contenido.isEmpty()) {
                Toast.makeText(this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            databaseReference = FirebaseDatabase.getInstance().getReference("publicación")
            val publicacion = Publicacion(titulo, contenido)
            databaseReference.child(titulo).setValue(publicacion).addOnSuccessListener {
                binding.textNombre.text.clear()
                binding.textoContenido.text.clear()

                Toast.makeText(this, "Publicación registrada", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CrearAnuncio, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al registrar publicación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

