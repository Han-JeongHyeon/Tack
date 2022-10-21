package com.example.myapplication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Repository(application : Application) {

    // Room Dao
    private val fishListDao = AppDatabase.getInstance(application)!!.getFishDao()

    // Use Room
    fun selectall(): LiveData<List<Fish>> {
        return fishListDao.getAll()
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

    companion object {
        private var instance: Repository? = null

        fun getInstance(application : Application): Repository? {
            if (instance == null) instance = Repository(application)
            return instance
        }
    }
}