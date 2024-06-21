package com.proyectomovil.snorb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyectomovil.snorb.model.ImageData

class ActivityReportesAdmin : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var imageAdapter: ImageAdapter
    private val databaseReference = FirebaseDatabase.getInstance().getReference("reportes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.privacidadDatosPersonales)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        imageAdapter = ImageAdapter(this, R.layout.item_images, mutableListOf())
        listView.adapter = imageAdapter

        loadImages()
    }

    private fun loadImages() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = mutableListOf<ImageData>()
                for (dataSnapshot in snapshot.children) {
                    val imageData = dataSnapshot.getValue(ImageData::class.java)
                    imageData?.let { images.add(it) }
                }
                imageAdapter.clear()
                imageAdapter.addAll(images)
                imageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores aquí
            }
        })
    }
}
