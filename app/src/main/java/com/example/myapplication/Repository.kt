package com.example.myapplication

import android.app.Application

class Repository(application : Application) {

    // Room Dao
    private val fishListDao = AppDatabase.getInstance(application)!!.getFishDao()

    // Use Room
    fun selectPaging(page : Int, pageSize : Int): List<Fish> {
        return fishListDao.getPage(page, pageSize)
    }

    suspend fun insertFishList(list : Fish) {
        fishListDao.insertAll(list)
    }

    companion object {
        private var instance: Repository? = null

        fun getInstance(application : Application): Repository? {
            if (instance == null) instance = Repository(application)
            return instance
        }
    }
}