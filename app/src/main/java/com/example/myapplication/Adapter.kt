package com.example.myapplication

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Adapter(private val context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    var datas = SparseArray<DateClassTest>()
//    var datas = mutableListOf<DateClassTest>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.textview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fishname: TextView = itemView.findViewById(R.id.fishname)
        private val fishprice: TextView = itemView.findViewById(R.id.fishprice)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_photo)

        fun bind(item: DateClassTest) {
            fishname.text = "이름 : ${item.name}"
            fishprice.text = "가격 : ${item.price}원"
            Glide.with(itemView).load(item.image).into(imgProfile)
        }
    }
}