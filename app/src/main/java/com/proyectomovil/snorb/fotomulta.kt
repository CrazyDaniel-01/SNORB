package com.proyectomovil.snorb

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import java.io.OutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class fotomulta : Fragment() {

    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase


    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            view?.findViewById<ImageView>(R.id.imageView)?.setImageBitmap(bitmap)
            saveImageToStorage(bitmap)
            uploadImageAndText(bitmap, "Texto asociado con la imagen")
        } else {
            Toast.makeText(requireContext(), "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance("https://fotomulta-80868-default-rtdb.firebaseio.com/")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fotomulta, container, false)
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

    private fun uploadImageAndText(bitmap: Bitmap, text: String) {
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
                    "description" to text
                )

                database.reference.child("images").push().setValue(imageInfo)
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
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fotomulta().apply {
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
        }
    }
}
