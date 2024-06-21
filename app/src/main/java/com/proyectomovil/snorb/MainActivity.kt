package com.proyectomovil.snorb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.proyectomovil.snorb.databinding.ActivityMainBinding
import com.proyectomovil.snorb.databinding.ActivityPerfilBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener{
            val nombreUsuario  = binding.usuario.text.toString()
            val contraseña  = binding.contra.text.toString()
            databaseReference = FirebaseDatabase.getInstance().getReference("Usuario")
            databaseReference.child(nombreUsuario).get().addOnSuccessListener {
                if (it.exists()){
                    val buscaUsuario = it.child("nombre").value
                    val buscaContraseña = it.child("contraseña").value
                    val tipoUsuario = it.child("tipoUsuario").value
                    if (buscaUsuario==nombreUsuario&&buscaContraseña==contraseña){
                        if (tipoUsuario!="Admin"){
                            if (tipoUsuario=="Ciudadano"){
                                val intent = Intent(this,ActivityCiudadano::class.java)
                                val sharedPref = this.getSharedPreferences("MiSharedPreferences", MODE_PRIVATE)
                                with(sharedPref.edit()){
                                    putString("Apodo",buscaUsuario.toString())
                                    apply()
                                }
                                startActivity(intent)
                            }else  {
                                val intent = Intent(this,ActivityRecolector::class.java)
                                val sharedPref = this.getSharedPreferences("MiSharedPreferences", MODE_PRIVATE)
                                with(sharedPref.edit()){
                                    putString("Apodo",buscaUsuario.toString())
                                    apply()
                                }
                                startActivity(intent)
                            }
                        }else{
                            val intent = Intent(this,ActivityAdmin::class.java)
                            val sharedPref = this.getSharedPreferences("MiSharedPreferences", MODE_PRIVATE)
                            with(sharedPref.edit()){
                                putString("Apodo",buscaUsuario.toString())
                                apply()
                            }
                            startActivity(intent)
                        }

                    }else{
                        Toast.makeText(this,"eeeh esta mal",Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(this,"eeeh esta mal",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{
                Toast.makeText(this,"eeehggg esta mal",Toast.LENGTH_SHORT).show()
            }










        }

        binding.BtnRegistrar.setOnClickListener{
            val intent = Intent(this,ActivityRegistrar::class.java)
            startActivity(intent)
        }


    }
}