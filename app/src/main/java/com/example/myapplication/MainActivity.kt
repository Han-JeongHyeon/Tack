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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    //데이터 베이스
    private var db : AppDatabase? = null

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

//        apiRequest()

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

        val requiredIds = ((pageValue) + 1..(pageValue + pageSize)).toList()
        val requestIds = requiredIds.minus(userDao.getPage(page, pageSize).map { it.fishNum }.toSet())

        var requestCnt = 0
        var requestCnt1 = 0

        val cache = Cache(File(cacheDir, "http_cache"), 10 * 1024 * 1024L)

        val client = OkHttpClient.Builder()
            .cache(cache)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(Interface::class.java)

        if (requestIds.isEmpty()) {
            addDataToRecyclerView()
            return
        }

//        var arrayFailNum = ArrayList<Int>()

        for (id in requestIds) {
            service.getName("$id").enqueue(object : Callback<FishName> {
                //api 요청 실패 처리
                override fun onFailure(call: Call<FishName>, t: Throwable) {
                    Log.d("Error", "" + t.toString())
                }
                //api 요청 성공 처리
                override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                    val result: FishName? = response.body()
                    requestCnt++
                    if (Random().nextInt(10) + 1 == 1) requestCnt1++ //arrayFailNum.add(id)
                    else {
                        userDao.insertAll(
                            Fishs(id, result!!.name.KRko, result.price.toInt(), result.image))
                    }
                    if (requestCnt == requestIds.size) {
//                        if (arrayFailNum.isNotEmpty()) {
//                            Toast.makeText(
//                                this@MainActivity,
//                                "${arrayFailNum}}번 값을 불러오지 못했습니다",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                        addDataToRecyclerView()
                    }
                    if (requestCnt1 == requestIds.size) binding.text.text = "데이터를 가져오지 못했습니다."
                }
            })
        }

    }

    private fun addDataToRecyclerView() {
        val userDao = db!!.userDao()

        userDao.getPage(page, pageSize).let {
            fishAdapter.submitList(it)
        }
    }

}