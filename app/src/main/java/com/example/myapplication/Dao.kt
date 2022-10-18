package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {

    @Query("SELECT * FROM Fishs")
    fun getAll(): List<Fishs>

    @Query("SELECT * FROM Fishs where fishNum <= :page * :pageSize + :pageSize")
    fun getPage(page: Int, pageSize : Int) : List<Fishs>

    @Insert
    suspend fun insertAll(vararg users: Fishs)

    @Delete
    fun delete(user: Fishs)
}