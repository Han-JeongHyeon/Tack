package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Response

class Repository(application : Application) {

    // Room Dao
    private val todoDao = AppDatabase.getInstance(application)!!.getFishDao()

    // Use Room
    fun roomSelectAllTodo(page : Int, pageSize : Int): List<Fishs> {
        return todoDao.getPage(page, pageSize)
    }

    fun roomSelectAll(): List<Fishs> {
        return todoDao.getAll()
    }

    suspend fun roomInsertTodo(todo: Fishs) {
        Log.d("TAG", "adsda$todo")
        todoDao.insertAll(todo)
    }

    companion object {
        private var instance: Repository? = null

        fun getInstance(application : Application): Repository? {
            if (instance == null) instance = Repository(application)
            return instance
        }
    }
}