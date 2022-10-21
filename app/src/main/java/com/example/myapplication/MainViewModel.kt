package com.example.myapplication

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.*
import kotlinx.coroutines.*

class MainViewModel(private val repository: Repository, application: Application) : ViewModel() {

    private val fishListDao = AppDatabase.getInstance(application)!!.getFishDao()

    private var page = 0
    private var pageSize = 20

    private var pageValue = 0

    private var _selectList = MutableLiveData<List<Fish>>()
    var selectList: LiveData<List<Fish>> = _selectList

    var selectListObserver: LiveData<List<Fish>> = fishListDao.getAll()

    var roomInput: MutableLiveData<String> = MutableLiveData()

    fun insertFishList(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        pageValue = page * pageSize

        if (pageValue + pageSize > 80) {
            return@launch
        }

        val requiredIds = ((pageValue) + 1..(pageValue + pageSize)).toList()
        val requestIds = requiredIds.minus(fishListDao.getPage(pageValue + pageSize).map { it.id }.toSet())

        roomInput.postValue("정보를 불러오는 중...")

        val requestApi = RetrofitObject.getRetrofitService(context)

        for (id in requestIds) {
            val apiResponse = requestApi.getName("$id")
            apiResponse.let {
                repository.insertFishList(Fish(id, it.name.KRko, it.price.toInt(), it.image, false))
            }
        }
        getFishList()
    }

    fun favorite(view: View, item : Fish) {
        val favorite : Button = view.findViewById(R.id.favorite)

        var background : Int

        viewModelScope.launch(Dispatchers.IO) {
            if (fishListDao.selectFavorite(item.id)) {
                background = R.drawable.favorite_border
                item.favorite = false
            } else {
                background = R.drawable.favorite
                item.favorite = true
            }
            repository.updateFavorite(item)
            withContext(Dispatchers.Main) {
                favorite.setBackgroundResource(background)
            }
        }
    }

    fun getFishList(){
        viewModelScope.launch(Dispatchers.IO){
            roomInput.postValue("")
            _selectList.postValue(repository.selectPaging(pageValue + pageSize))
            page++
        }
    }

    class Factory(private val application : Application) : ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(Repository.getInstance(application)!!,application) as T
        }
    }

}