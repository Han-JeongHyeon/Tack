package com.example.myapplication

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {

    @Query("SELECT * FROM Fish")
    fun getAll(): List<Fish>

    @Query("SELECT * FROM Fish where fishNum <= :page * :pageSize + :pageSize")
    fun getPage(page: Int, pageSize : Int) : List<Fish>

    @Query("select * from Favorite where id = :id")
    fun selectFavorite(id : Int): Boolean

    @Insert
    suspend fun insertAll(vararg users: Fish)

    @Insert
    suspend fun insertFavorite(vararg users: Favorite)

    @Update
    suspend fun updateFavorite(vararg users: Favorite)

    @Delete
    fun delete(user: Favorite)
}