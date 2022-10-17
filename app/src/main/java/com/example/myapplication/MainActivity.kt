package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.CoroutinesRoom
import androidx.room.Room
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var fishAdapter = Adapter()

    var page = 0
    private var pageSize = 20

//    private var viewModelJob : CompletableJob = Job()
//    private val coroutineScope : CoroutineScope = CoroutineScope(
//        viewModelJob + Dispatchers.Main
//    )

    //데이터 베이스
    private var db : AppDatabase? = null
    private var retrofit: Retrofit? = null

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Recycler.adapter = fishAdapter

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "fish"
        ).allowMainThreadQueries().build()

        val cache = Cache(File(cacheDir, "http_cache"), 10 * 1024 * 1024L)

        val client = OkHttpClient.Builder()
            .cache(cache)
            .build()

        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        apiRequest()

        binding.Recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.Recycler.canScrollVertically(1)) {
                    page++
                    apiRequest()
                }
            }
        })

    }

    private fun apiRequest() {
        val pageValue = page * pageSize

        if (pageValue + pageSize > 80) {
            return
        }

        val userDao = db!!.userDao()

        val service = retrofit!!.create(Interface::class.java)

        val requiredIds = ((pageValue) + 1..(pageValue + pageSize)).toList()
        val requestIds = requiredIds.minus(userDao.getPage(page, pageSize).map { it.fishNum }.toSet())

        if (requestIds.isEmpty()) {
            addDataToRecyclerView()
            return
        }

        CoroutineScope(Dispatchers.Main).launch{
            for (id in requestIds) {
                val apiResponse = service.getName("$id")
                apiResponse.let {
                    userDao.insertAll(Fishs(id, it.name.KRko, it.price.toInt(), it.image))
                }
            }
            addDataToRecyclerView()
        }
    }

    private fun addDataToRecyclerView() {
        val userDao = db!!.userDao()

        userDao.getPage(page, pageSize).let {
            fishAdapter.submitList(it)
        }
    }

}