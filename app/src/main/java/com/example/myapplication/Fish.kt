package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Fish(
    @PrimaryKey val id : Int,
    val name : String,
    val Price : Int,
    val image : String,
    var favorite : Boolean
)