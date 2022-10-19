package com.example.myapplication

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.*

class MainViewModel(private val repository: Repository, application: Application) : ViewModel() {

    private val fishListDao = AppDatabase.getInstance(application)!!.getFishDao()

    private var page = 0
    private var pageSize = 20

    private var _selectList = MutableLiveData<List<Fish>>()
    var selectList: LiveData<List<Fish>> = _selectList

    var roomInput: MutableLiveData<String> = MutableLiveData()

    fun insertFishList(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        if (page * pageSize + pageSize > 80) {
            return@launch
        }

        val requiredIds = ((page * pageSize) + 1..(page * pageSize + pageSize)).toList()
        val requestIds = requiredIds.minus(fishListDao.getPage(0,20).map { it.fishNum }.toSet())

        roomInput.postValue("정보를 불러오는 중...")

        val requestApi = RetrofitObject.getRetrofitService(context)

        for (id in requestIds) {
            val apiResponse = requestApi.getName("$id")
            apiResponse.let {
                repository.insertFishList(Fish(id, it.name.KRko, it.price.toInt(), it.image))
            }
        }
        getFishList()
    }

    private fun getFishList(){
        viewModelScope.launch(Dispatchers.IO){
            roomInput.postValue("")
            _selectList.postValue(repository.selectPaging(page, pageSize))
            page++
        }
    }

    class Factory(private val application : Application) : ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(Repository.getInstance(application)!!,application) as T
        }
    }

}