package com.example.myapplication

import androidx.recyclerview.widget.DiffUtil

class DiffUtil : DiffUtil.ItemCallback<Fishs>() {
    override fun areItemsTheSame(
        oldItem: Fishs,
        newItem: Fishs
    ): Boolean {
        return oldItem.fishNum == newItem.fishNum
    }

    override fun areContentsTheSame(
        oldItem: Fishs,
        newItem: Fishs
    ): Boolean {
        return oldItem == newItem
    }
}