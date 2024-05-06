package com.proyectomovil.snorb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btnLogin)
        btn.setOnClickListener{
            val intent = Intent(this,VistaAdmin::class.java)
            startActivity(intent)
        }


    }
}