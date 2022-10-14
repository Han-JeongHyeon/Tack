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


/*
1. SqliteHelper -> Room library
2. AsyncTask -> kotlin coroutine
3. Recyclerview.Adapter -> ListAdapter (DiffUtil)
 */

class Adapter(private val context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    var datas = mutableListOf<Fishs>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.textview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fishname: TextView = itemView.findViewById(R.id.fishname)
        private val fishprice: TextView = itemView.findViewById(R.id.fishprice)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_photo)

        fun bind(item: Fishs) {
            fishname.text = "이름 : ${item.name}"
            fishprice.text = "가격 : ${item.Price}원"
            Glide.with(itemView).load(item.image).into(imgProfile)
        }
    }
}