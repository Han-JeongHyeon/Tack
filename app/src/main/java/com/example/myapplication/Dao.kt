package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {

    @Query("SELECT * FROM Fish")
    fun getAll(): List<Fish>

    @Query("SELECT * FROM Fish where fishNum <= :page * :pageSize + :pageSize")
    fun getPage(page: Int, pageSize : Int) : List<Fish>

    @Insert
    suspend fun insertAll(vararg users: Fish)

    @Delete
    fun delete(user: Fish)
}