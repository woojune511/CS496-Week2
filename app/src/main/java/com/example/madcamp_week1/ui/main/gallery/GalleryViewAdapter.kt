package com.example.madcamp_week1.ui.main.gallery

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week1.MainActivity
import com.example.madcamp_week1.R
import java.io.File
import kotlin.coroutines.coroutineContext

class GalleryViewAdapter(private val context : Context, private val imageList: MutableList<String>, val itemLongClick: (Int) -> Boolean)
    : RecyclerView.Adapter<GalleryViewAdapter.Holder>() {

    inner class Holder(itemView: View, itemLongClick: (Int) -> Boolean) : RecyclerView.ViewHolder(itemView) {
        private val imageView : ImageView = itemView.findViewById<ImageView>(R.id.id_image)

        fun bind(img_path : String, position : Int){
            Glide.with(context)
                .load(File(img_path))
                .fitCenter()
                .override(300, 300)
                .placeholder(R.drawable.image_load)
                .into(imageView)
            imageView.setOnLongClickListener{ itemLongClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return Holder(view, itemLongClick)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(imageList[position], position)
    }

    override fun getItemCount(): Int = imageList.size

}

