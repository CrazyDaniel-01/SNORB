package com.proyectomovil.snorb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.proyectomovil.snorb.model.ImageData

class ImageAdapter(context: Context, private val resource: Int, private val images: List<ImageData>) :
    ArrayAdapter<ImageData>(context, resource, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val descriptionView = view.findViewById<TextView>(R.id.textViewDescription)
        val dateView = view.findViewById<TextView>(R.id.textViewDate)
        val locationView = view.findViewById<TextView>(R.id.textViewLocation)

        val imageData = getItem(position)

        Glide.with(context).load(imageData?.imageUrl).into(imageView)
        descriptionView.text = imageData?.description
        dateView.text = imageData?.date
        locationView.text = imageData?.location

        return view
    }
}
