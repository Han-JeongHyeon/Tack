package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding

class asdasd(private val context: Context) : ListAdapter<Fishs, asdasd.ContactViewHolder>(ContactComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fishname: TextView = itemView.findViewById(R.id.fishname)
        private val fishprice: TextView = itemView.findViewById(R.id.fishprice)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_photo)

        fun bind(contact: Fishs) {
            fishname.text = "이름 : ${contact.name}"
            fishprice.text = "가격 : ${contact.Price}원"
            Glide.with(itemView).load(contact.image).into(imgProfile)
        }

        companion object {
            fun create(parent: ViewGroup): ContactViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.textview, parent, false)
                return ContactViewHolder(view)
            }
        }
    }

    class ContactComparator : DiffUtil.ItemCallback<Fishs>() {
        override fun areItemsTheSame(oldItem: Fishs, newItem: Fishs): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Fishs, newItem: Fishs): Boolean {
            return oldItem.fishNum == newItem.fishNum
        }
    }
}