package com.example.myapplication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Repository(private val application : Application, private val retrofit: RetrofitObject) {

    val retrofitService = retrofit.getRetrofitService()

    // Room Dao
    private val fishListDao = AppDatabase.getInstance(application)!!.getFishDao()

    // Use Room
    fun selectAll(): LiveData<List<Fish>> {
        return fishListDao.getAll()
    }

    fun selectFavorite(position: Int): Boolean{
        return fishListDao.selectFavorite(position)
    }

    fun selectPaging(pageSize : Int): List<Fish> {
        return fishListDao.getPage(pageSize)
    }

    suspend fun insertFishList(list : Fish) {
        fishListDao.insertAll(list)
    }

    suspend fun updateFavorite(list : Fish) {
        fishListDao.updateFavorite(list)
    }
}