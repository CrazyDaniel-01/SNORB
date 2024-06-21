package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyectomovil.snorb.R
import com.proyectomovil.snorb.Usuario

class ActivityListaUsuarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_usuarios)

        var lv_usuarios = findViewById<ListView>(R.id.lvUsuarios)
        var list_usuarios: ArrayList<String> = ArrayList()
        var database = FirebaseDatabase.getInstance()
        var usuariosRef = database.getReference("Usuario")

        var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_usuarios)
        lv_usuarios.adapter = adaptador

        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adaptador.clear()
                for (registro in snapshot.children) {
                    val usuario = registro.getValue(Usuario::class.java)
                    val textoUsuario = "nombre: " + usuario?.nombre + ", direccion: " + usuario?.direccion
                    list_usuarios.add(textoUsuario)
                }
                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al consultar los usuarios: ${error.message}")
            }
        })
    }
}