package com.example.myapplication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


/*
1. SqliteHelper -> Room library
2. AsyncTask -> kotlin coroutine
3. Recyclerview.Adapter -> ListAdapter (DiffUtil)
 */

class Adapter : ListAdapter<Fishs, Adapter.ContactViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fishName: TextView = itemView.findViewById(R.id.fishname)
        private val fishPrice: TextView = itemView.findViewById(R.id.fishprice)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_photo)

        @SuppressLint("SetTextI18n")
        fun bind(fish : Fishs) {
            fishName.text = "이름 : ${fish.name}"
            fishPrice.text = "가격 : ${fish.Price}원"
            Glide.with(itemView).load(fish.image).into(imgProfile)
        }

        companion object {
            fun create(parent: ViewGroup): ContactViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.textview, parent, false)
                return ContactViewHolder(view)
            }
        }

    }
}