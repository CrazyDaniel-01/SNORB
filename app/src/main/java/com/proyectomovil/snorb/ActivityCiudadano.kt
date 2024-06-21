package com.proyectomovil.snorb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.proyectomovil.snorb.databinding.ActivityVistaUsuarioBinding

class ActivityCiudadano : AppCompatActivity() {
    private lateinit var binding: ActivityVistaUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(rutasAdmin())
        binding.bottomNavigationViewCiudadano.setOnItemSelectedListener {  menuItem ->
            when (menuItem.itemId) {

                R.id.rutas_admin -> replaceFragment(rutasAdmin())
                R.id.fotomulta_admin -> replaceFragment(fotomultaAdmin())
                R.id.anuncios_admin -> replaceFragment(AnuncioAdmin())
                R.id.ajustes_ciudadano -> replaceFragment(AjustesCiudadano())

                else ->{
                }
            }
            true }


    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameCiudadano,fragment)
        fragmentTransaction.commit()


    }
}