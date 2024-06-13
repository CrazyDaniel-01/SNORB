package com.proyectomovil.snorb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.proyectomovil.snorb.databinding.ActivityRegistrarBinding

class ActivityRegistrar : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener{
            val nombre = binding.nom.text.toString()
            val direccion = binding.dir.text.toString()
            val codigoPostal =binding.cop.text.toString()
            val contraseña = binding.con.text.toString()
            val tipoUsuario= "Ciudadano"

            databaseReference =FirebaseDatabase.getInstance().getReference("Usuario")
            val usuario = Usuario(nombre, direccion, contraseña, codigoPostal, tipoUsuario)
            databaseReference.child(nombre).setValue(usuario).addOnSuccessListener {
                binding.nom.text.clear()
                binding.dir.text.clear()
                binding.cop.text.clear()
                binding.con.text.clear()

                Toast.makeText(this,"UsuarioRegistrado",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ActivityRegistrar,MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"ñeee está mal",Toast.LENGTH_SHORT).show()
            }
        }
    }
}