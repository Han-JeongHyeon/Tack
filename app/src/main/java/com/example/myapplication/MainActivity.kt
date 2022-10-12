package com.example.myapplication

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var fishAdapter: Adapter
    val datas = mutableListOf<DateClassTest>()
    val datas1 = mutableListOf<DateClassTest>()

    lateinit var dbHelper: SqliteHelper
    lateinit var database: SQLiteDatabase

    var numCheckArray = ArrayList<Int>()

    var page = 0
    val pageSize = 20

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RecyclerView 연결
        fishAdapter = Adapter(this)
        binding.Recycler.adapter = fishAdapter

        dbHelper = SqliteHelper(this, "FishName.db", null, 1)
        database = dbHelper.writableDatabase

        binding.btn.setOnClickListener{
            val start = fishAdapter.datas[0].num / pageSize
            val end = fishAdapter.datas.size
            fishAdapter.datas.clear()
            fishAdapter.notifyDataSetChanged()
            var asyncTack = AsyncTack(this, start, end)
            val dbValue = asyncTack.execute()

            setRecyclerView(dbValue.get())
        }

        api()

        binding.Recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.Recycler.canScrollVertically(1)) {
                    page++
                    api()
                }
            }
        })

    }

    fun api() {
        if (page * pageSize + pageSize > 80){
            return
        }

        var asyncTack = AsyncTack(this, page, pageSize)
        val dbValue = asyncTack.execute()

        val requiredIds = ((page * pageSize)+1..(page * pageSize + pageSize)).toList()
        val requestIds = requiredIds.minus(dbValue.get().map { it.num }.toSet())

        var requestCnt = 0
        var requestCnt1 = 0

        val retrofit = Retrofit.Builder().baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(FishName_interface::class.java)

        if (requestIds.isEmpty() && fishAdapter.datas.size + dbValue.get().size == page * pageSize + pageSize) {
            setRecyclerView(dbValue.get())
            return
        }

        setRecyclerView(dbValue.get())

        for (id in requestIds) {
            service.getName("${id}").enqueue(object: Callback<FishName>{
                //api 요청 실패 처리
                override fun onFailure(call: Call<FishName>, t: Throwable) {
                    Log.d("Error", ""+t.toString())
                }
                //api 요청 성공 처리
                override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                    var result: FishName? = response.body()
                    if(java.util.Random().nextInt(10) + 1 == 1) requestCnt++
                    else{
                        var query = "INSERT INTO animals('num','name','price','image') values('${id}','${result?.name?.KRko}','${result?.price}','${result?.image}')"
                        database.execSQL(query)
                        requestCnt1++
                        datas1.apply {
                            add(DateClassTest(id,"${result?.name?.KRko}","${result?.price}","${result?.image}"))
                        }
                    }
                    if (requestCnt1+requestCnt == requestIds.size) {
                        setRecyclerView(datas1)
                    }
                }
            })
        }

    }

    fun setRecyclerView(result : MutableList<DateClassTest>) {
        addDataToRecyclerView(result)
    }

    private fun addDataToRecyclerView(result : MutableList<DateClassTest>) {
        val prevSize = fishAdapter.datas.size
        result?.let {
            fishAdapter.datas.addAll(it)
        }
        fishAdapter.notifyItemRangeInserted(prevSize+1, result.size)
        datas1.let {
            it.clear()
        }
    }
}