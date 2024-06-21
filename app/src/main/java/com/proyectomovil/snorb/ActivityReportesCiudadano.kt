package com.proyectomovil.snorb

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyectomovil.snorb.model.ImageData

class ActivityReportesCiudadano : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var imageAdapter: ImageAdapter
    private val databaseReference = FirebaseDatabase.getInstance().getReference("reportes")
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_ciudadano)

        listView = findViewById(R.id.lvReportesCiudadano)
        imageAdapter = ImageAdapter(this, R.layout.item_images, mutableListOf())
        listView.adapter = imageAdapter

        val sharedPref = this.getSharedPreferences("MiSharedPreferences", MODE_PRIVATE)
        username = sharedPref.getString("Apodo", "")

        Log.d("ActivityReportesCiudadano", "Username: $username")

        loadImages()
    }

    private fun loadImages() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = mutableListOf<ImageData>()
                for (dataSnapshot in snapshot.children) {
                    val imageData = dataSnapshot.getValue(ImageData::class.java)
                    if (imageData?.userName == username) {
                        imageData?.let { images.add(it) }
                    }
                }
                Log.d("ActivityReportesCiudadano", "Loaded ${images.size} images for username: $username")

                imageAdapter.clear()
                imageAdapter.addAll(images)
                imageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ActivityReportesCiudadano", "Database error: $error")
            }
        })
    }
}
