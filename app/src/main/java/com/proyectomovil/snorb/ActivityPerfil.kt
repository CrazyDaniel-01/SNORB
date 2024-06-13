package com.proyectomovil.snorb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.proyectomovil.snorb.databinding.ActivityPerfilBinding

class ActivityPerfil : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = this.getSharedPreferences("MiSharedPreferences", MODE_PRIVATE)
        val usuario = sharedPref.getString("Apodo", "")
        leerDatos(usuario.toString())

        binding.btnGuardarCambios.setOnClickListener{
            val nombre = binding.etNombreUsuario.text.toString()
            val direccion = binding.etDireccion.text.toString()
            val codigoPostal =binding.etNumero.text.toString()
            val contraseña = binding.etContra.text.toString()
            val tipoUsuario= "Ciudadano"


            databaseReference =FirebaseDatabase.getInstance().getReference("Usuario")
            val usuario = Usuario(nombre, direccion, contraseña, codigoPostal, tipoUsuario)
            databaseReference.child(nombre).setValue(usuario).addOnSuccessListener {

                Toast.makeText(this,"Cambios guardados",Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"ñeee está mal",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun leerDatos(nombre: String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuario")
        databaseReference.child(nombre).get().addOnSuccessListener {
            if (it.exists()){
                val nombreUsuario = it.child("nombre").value
                val direccion = it.child("direccion").value
                val codigoPostal = it.child("codigoPostal").value
                val contraseña = it.child("contraseña").value
                val tipoUsuario = it.child("tipoUsuario").value


                binding.etNombreUsuario.text.clear()
                binding.etNombreUsuario.setText(nombreUsuario.toString())
                binding.etDireccion.setText(direccion.toString())
                binding.etNumero.setText(codigoPostal.toString())
                binding.etContra.setText(contraseña.toString())
                binding.tvTipo.setText(tipoUsuario.toString())
            }else{
                Toast.makeText(this,"eeeh esta mal",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"eeehggg esta mal",Toast.LENGTH_SHORT).show()
        }
    }





}