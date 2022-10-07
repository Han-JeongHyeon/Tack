package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import com.example.myapplication.databinding.ActivityMainBinding
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    //뷰 바인딩
    private lateinit var binding: ActivityMainBinding
    // SQLite
    // Room db
    // CamelCase
    //어뎁터 연결
    lateinit var fishAdapter: Adapter
//    val datas = mutableListOf<DateClassTest>()
//    val datas = SparseArray<DateClassTest>()
    // SnakeStyle
    //SparseArray
    var nameArray : SparseArray<DateClassTest>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RecyclerView 연결
        fishAdapter = Adapter(this)
        binding.Recycler.adapter = fishAdapter

        //retrofit 설장
        val retrofit = Retrofit.Builder().baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build();
        val service = retrofit.create(FishName_interface::class.java);

        nameArray = SparseArray<DateClassTest>()

        for (i in 0 until 80) {
            service.getName("${i + 1}").enqueue(object: Callback<FishName>{
                //api 요청 실패 처리
                override fun onFailure(call: Call<FishName>, t: Throwable) {
                    Log.d("Error", ""+t.toString())
                }
                //api 요청 성공 처리
                override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                    var result: FishName? = response.body()
                    //Array에 값 저장
                    nameArray?.append(i,
                        DateClassTest(
                        "${result?.name?.KRko}",
                        "${result?.price}",
                        "${result?.image}"
                        )
                    )
                    if (nameArray?.size() == 80) {
                        addDataToRecyclerView()
                    }
                }
            })
        }
    }

    private fun addDataToRecyclerView() {
        nameArray?.let {
            fishAdapter.datas = it
            fishAdapter.notifyDataSetChanged()
        }
    }
}