package com.proyectomovil.snorb

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class fotomultaAdmin : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var locationManager: LocationManager
    private var location: Location? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(loc: Location) {
            location = loc
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            view?.findViewById<ImageView>(R.id.imageView)?.setImageBitmap(bitmap)

            val editText = view?.findViewById<EditText>(R.id.editText)
            val text = editText?.text.toString()

            val currentDate = getCurrentDate()
            val currentTime = getCurrentTime()
            val currentLocation = getCurrentLocation()
            val userName = getUserName()

            uploadImageAndText(bitmap, text, currentDate, currentTime, currentLocation, userName)
        } else {
            Toast.makeText(requireContext(), "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance("https://fotomulta-80868-default-rtdb.firebaseio.com/")
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fotomulta_admin, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                takePicture.launch(null)
            }
        }

        return view
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentLocation(): String {
        return location?.let { "${it.latitude}, ${it.longitude}" } ?: "Unknown location"
    }
    private fun getUserName(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MiSharedPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Apodo", "Unknown User") ?: "Unknown User"
    }

    private fun saveImageToStorage(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Fotomulta_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Fotomulta")
        }

        val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            Toast.makeText(requireContext(), "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndText(bitmap: Bitmap, text: String, date: String, time: String, location: String, userName: String) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/Fotomulta_${System.currentTimeMillis()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        Log.d("Firebase", "Comenzando a subir la imagen...")

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e("Firebase", "Error al subir la imagen", it)
        }.addOnSuccessListener { taskSnapshot ->
            Log.d("Firebase", "Imagen subida con éxito, obteniendo URL de descarga...")
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                Log.d("Firebase", "URL de descarga obtenida: $imageUrl")

                val imageInfo = hashMapOf(
                    "imageUrl" to imageUrl,
                    "description" to text,
                    "date" to date,
                    "time" to time,
                    "location" to location,
                    "userName" to userName
                )

                database.reference.child("reportes").child("Reporte"+" "+time+" "+date).setValue(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Imagen y texto subidos con éxito a Realtime Database", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Error al subir la información a Realtime Database", e)
                    }
            }.addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener la URL de descarga", e)
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1002

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fotomultaAdmin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                takePicture.launch(null)
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
                }
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}