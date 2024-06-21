package com.proyectomovil.snorb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.proyectomovil.snorb.databinding.ActivityRecolectorBinding

class ActivityRecolector : AppCompatActivity() {
    private lateinit var binding: ActivityRecolectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecolectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(rutasAdmin())
        binding.bottomNavigationViewRecolector.setOnItemSelectedListener {  menuItem ->
            when (menuItem.itemId) {

                R.id.rutas_admin -> replaceFragment(rutasAdmin())
                R.id.fotomulta_admin -> replaceFragment(fotomultaAdmin())
                R.id.anuncios_admin -> replaceFragment(AnuncioAdmin())
                R.id.ajustes_recolector -> replaceFragment(AjustesRecolector())

                else ->{
                }
            }
            true }


    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutRecolector,fragment)
        fragmentTransaction.commit()


    }
}