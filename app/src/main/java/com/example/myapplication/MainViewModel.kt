package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class MainViewModel(private val context: Context, private val repository: Repository, private val retrofit: RetrofitObject) : ViewModel() {

    private var page = 0
    private var pageSize = 20

    private var pageValue = 0

    private var _selectList = MutableLiveData<List<Fish>>()
    var selectList: LiveData<List<Fish>> = _selectList

    var selectListObserver: LiveData<List<Fish>> = repository.selectAll()

    var roomInput: MutableLiveData<String> = MutableLiveData()

    var timeInput: MutableLiveData<String> = MutableLiveData()

    fun insertFishList() = viewModelScope.launch(Dispatchers.IO) {
        pageValue = page * pageSize

        if (pageValue + pageSize > 80) {
            return@launch
        }

        val requiredIds = ((pageValue) + 1..(pageValue + pageSize)).toList()
        val requestIds = requiredIds.minus(repository.selectPaging(pageValue + pageSize).map { it.id }.toSet())

        roomInput.postValue("정보를 불러오는 중...")

        for (id in requestIds) {
            val apiResponse = retrofit.getRetrofitService().getName("$id")
            apiResponse.let {
                repository.insertFishList(Fish(id, it.name.KRko, it.price.toInt(), it.image, false))
            }
        }
        getFishList()
        page++
    }

    fun getFishList(){
        viewModelScope.launch(Dispatchers.IO){
            roomInput.postValue("")
            _selectList.postValue(repository.selectPaging(pageValue + pageSize))
        }
    }

    fun favorite(view: View, item : Fish) {
        val favorite : Button = view.findViewById(R.id.favorite)

        var background : Int

        viewModelScope.launch(Dispatchers.IO) {
            if (repository.selectFavorite(item.id)) {
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

    var second : Int = 0
    var min : Int = 0
    var hour : Int = 0

    @SuppressLint("SimpleDateFormat")
    fun dateTimeToMillSec(): Timer {
        val taskTimer = timer(period = 1000) {
            Log.d("TAG", "$second")
            if (second == 60) {
                min++
                second = 0
            }
            if (min == 60) {
                hour++
                min = 0
            }

            timeInput.postValue("접속 후 ${hour}시 ${min}분 ${second}초가 지났습니다")
            second++
        }

        return taskTimer

    }



}