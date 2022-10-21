package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {

    @Query("SELECT * FROM Fish")
    fun getAll(): LiveData<List<Fish>>

    @Query("SELECT * FROM Fish where id <= :page * :pageSize + :pageSize")
    fun getPage(page: Int, pageSize : Int) : List<Fish>

    @Query("SELECT * FROM Fish where id <= :page * :pageSize + :pageSize")
    fun getPageList(page: Int, pageSize : Int) : LiveData<List<Fish>>

    @Query("select favorite from Fish where id = :id")
    fun selectFavorite(id : Int): Boolean

    @Insert
    suspend fun insertAll(vararg users: Fish)

//    @Insert
//    suspend fun insertFavorite(vararg users: Favorite)

    @Update
    suspend fun updateFavorite(vararg users: Fish)

//    @Delete
//    fun delete(user: Favorite)
}