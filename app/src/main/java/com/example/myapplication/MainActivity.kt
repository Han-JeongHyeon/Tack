package com.example.myapplication

import android.annotation.SuppressLint
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.size
import androidx.viewbinding.ViewBinding
import com.example.myapplication.databinding.ActivityMainBinding
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    //뷰 바인딩
    private lateinit var binding: ActivityMainBinding

    //어뎁터 연결
    lateinit var profileAdapter: Adapter
    val datas = mutableListOf<DateClassTest>()

    //SparseArray
    var name_arr : SparseArray<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RecyclerView 연결
        profileAdapter = Adapter(this)
        binding.Recycler.adapter = profileAdapter

        //retrofit 설장
        val retrofit = Retrofit.Builder().baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build();
        val service = retrofit.create(FishName_interface::class.java);

        name_arr = SparseArray(0)

        for (i in 1..80) {
            service.getName("$i").enqueue(object: Callback<FishName>{
                //api 요청 실패 처리
                override fun onFailure(call: Call<FishName>, t: Throwable) {
                    Log.d("Error", ""+t.toString())
                }
                //api 요청 성공 처리
                override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                    var result: FishName? = response.body()
                    //Array에 값 저장
                    name_arr?.append(i,"${result?.name?.KRko}/${result?.image}@${result?.price}")
                    //마지막 데이터가 있는지 체크
                    if (name_arr?.get(80) != null) {
                        RecyclerV()
                    }
                }
            })
        }
    }

    //RecyclerView에 추가하기
    private fun RecyclerV() {
        for (i in 1..80) {
            Log.d("TAG", "${name_arr?.get(i)}")
            datas.apply {
                add(DateClassTest("${name_arr?.get(i)}"))
            }
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
    }
}