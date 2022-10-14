package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {

    @Query("SELECT * FROM Fishs")
    fun getAll(): List<Fishs>

    @Query("SELECT * FROM Fishs " +
            "where fishNum > :page * :pageSize and fishNum <= :page * :pageSize + :pageSize " +
            "limit 0, :pageSize")
    fun getWhere(page: Int, pageSize : Int) : List<Fishs>

    @Insert
    fun insertAll(vararg users: Fishs)

    @Delete
    fun delete(user: Fishs)
}