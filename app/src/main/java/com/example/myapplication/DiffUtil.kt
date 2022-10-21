package com.example.myapplication

import androidx.recyclerview.widget.DiffUtil

class DiffUtil : DiffUtil.ItemCallback<Fish>() {
    override fun areItemsTheSame(
        oldItem: Fish,
        newItem: Fish
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Fish,
        newItem: Fish
    ): Boolean {
        return oldItem == newItem
    }
}