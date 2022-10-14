package com.example.myapplication

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.databinding.ActivityMainBinding
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var fishAdapter: Adapter

    var page = 0
    val pageSize = 20

    //데이터 베이스
    var db : AppDatabase? = null

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fishAdapter = Adapter(this)
        binding.Recycler.adapter = fishAdapter

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "fish"
        ).allowMainThreadQueries().build()

        api()

        binding.Recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.Recycler.canScrollVertically(1)) {
                    page++
                    api()
                }
            }
        })

    }

    fun api() {
        if (page * pageSize + pageSize > 80) {
            return
        }

        val userDao = db!!.userDao()

        val requiredIds = ((page * pageSize) + 1..(page * pageSize + pageSize)).toList()
        val requestIds = requiredIds.minus(userDao.getWhere(page, pageSize).map { it.fishNum }.toSet())

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

        for (id in requestIds) {
            service.getName("$id").enqueue(object : Callback<FishName> {
                //api 요청 실패 처리
                override fun onFailure(call: Call<FishName>, t: Throwable) {
                    Log.d("Error", "" + t.toString())
                }
                //api 요청 성공 처리
                override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                    var result: FishName? = response.body()
                    if (Random().nextInt(10) + 1 == 1) requestCnt++
                    else {
                        userDao.insertAll(
                            Fishs("$id".toInt(), "${result!!.name.KRko}", "${result?.price}".toInt(), "${result?.image}"))
                        requestCnt1++
                    }
                    if (requestCnt1 + requestCnt == requestIds.size) addDataToRecyclerView()

                    if (requestCnt == requestIds.size) binding.text.text = "데이터를 가져오지 못했습니다."
                }
            })
        }

    }

    private fun addDataToRecyclerView() {
        val userDao = db!!.userDao()

        val prevSize = fishAdapter.datas.size

        userDao.getWhere(page, pageSize).let {
            fishAdapter.datas.addAll(it)
        }
//        fishAdapter.notifyDataSetChanged()
        fishAdapter.notifyItemRangeInserted(prevSize, userDao.getWhere(page,pageSize).size)
    }

}