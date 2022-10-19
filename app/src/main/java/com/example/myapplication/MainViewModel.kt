package com.example.myapplication

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainViewModel(private val repository: Repository, application: Application) : ViewModel() {

    private val todoDao = AppDatabase.getInstance(application)!!.getFishDao()

    private var page = 0
    var pageSize = 20

    private var _roomTodoList = MutableLiveData<List<Fishs>>()
    var roomTodoList: LiveData<List<Fishs>> = _roomTodoList

    var roomInput: MutableLiveData<String> = MutableLiveData()

    fun insertRoom(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        if (page * pageSize + pageSize > 80) {
            return@launch
        }

        val requiredIds = ((page * pageSize) + 1..(page * pageSize + pageSize)).toList()
        val requestIds = requiredIds.minus(todoDao.getPage(page,pageSize).map { it.fishNum }.toSet())

        roomInput.postValue("정보를 불러오는 중...")

        for (id in requestIds) {
            val apiResponse = RetrofitObject.getRetrofitService(context).getName("$id")
            apiResponse.let {
                repository.roomInsertTodo(Fishs(id, it.name.KRko, it.price.toInt(), it.image))
            }
        }
        getFishName()
    }

    private fun getFishName(){
        viewModelScope.launch(Dispatchers.IO){
            roomInput.postValue("")
            _roomTodoList.postValue(repository.roomSelectAllTodo(page, pageSize))
            page++
        }
    }

    class Factory(private val application : Application) : ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(Repository.getInstance(application)!!,application) as T
        }
    }

}