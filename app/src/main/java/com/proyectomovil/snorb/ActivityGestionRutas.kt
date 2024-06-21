package com.proyectomovil.snorb

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class ActivityGestionRutas : AppCompatActivity(),OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView2) as SupportMapFragment
        mapFragment.getMapAsync(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestion_rutas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.privacidadDatosPersonales)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Aquí puedes inicializar y configurar el mapa
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setMinZoomPreference(10f)
        // Añadir marcadores, líneas, etc.
    }
}