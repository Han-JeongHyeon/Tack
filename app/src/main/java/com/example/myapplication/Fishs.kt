package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity
data class Fishs(
    @PrimaryKey val fishNum : Int,
    val name : String,
    val Price : Int,
    val image : String
)