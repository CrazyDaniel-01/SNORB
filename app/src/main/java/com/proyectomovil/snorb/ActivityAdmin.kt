package com.proyectomovil.snorb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.proyectomovil.snorb.databinding.ActivityVistaAdminBinding

class ActivityAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityVistaAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(rutasAdmin())

        binding.bottomNavigationViewAdmin.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.rutas_admin -> replaceFragment(rutasAdmin())
                R.id.fotomulta_admin -> replaceFragment(fotomultaAdmin())
                R.id.anuncios_admin -> replaceFragment(AnuncioAdmin())
                R.id.ajustes_admin -> replaceFragment(AjustesAdmin())

                else ->{
                }
            }
            true
        }

    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layoutAdmin,fragment)
        fragmentTransaction.commit()


    }
}