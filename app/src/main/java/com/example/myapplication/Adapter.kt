package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.TextviewBinding
import kotlinx.coroutines.*

/*
1. SqliteHelper -> Room library
2. AsyncTask -> kotlin coroutine
3. Recyclerview.Adapter -> ListAdapter (DiffUtil)
 */

class Adapter(val application: Application) :
    ListAdapter<Fish, Adapter.ContactViewHolder>(DiffUtil()) {

    // 생성된 뷰 홀더에 값 지정
    inner class ContactViewHolder(private val binding: TextviewBinding, application: Application) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(fish: Fish) {
            binding.fishname.text = "이름 : ${fish.name}"
            binding.fishprice.text = "가격 : ${fish.Price}원"
            Glide.with(itemView).load(fish.image).into(binding.imgPhoto)

            if (fish.favorite) binding.favorite.setBackgroundResource(R.drawable.favorite)
            else binding.favorite.setBackgroundResource(R.drawable.favorite_border)
        }

    }

    // 어떤 xml 으로 뷰 홀더를 생성할지 지정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = TextviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding, application)
    }

    // 뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
        val favorite : Button = holder.itemView.findViewById(R.id.favorite)
        favorite.setOnClickListener {
            listener?.onItemClick(holder.itemView, getItem(position))
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, item: Fish)
//        fun onItemLongClick(v: View, item: Fish)
    }

    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

}