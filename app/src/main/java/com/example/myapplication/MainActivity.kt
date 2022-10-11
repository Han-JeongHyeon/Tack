package com.example.myapplication

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.w3c.dom.Text
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

    lateinit var dbHelper: SqliteHelper
    lateinit var database: SQLiteDatabase

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

        asyncTask.execute()

    }

    fun api(numPosition : Int) {
        val retrofit = Retrofit.Builder().baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(FishName_interface::class.java)

        service.getName("$numPosition").enqueue(object: Callback<FishName>{
            //api 요청 실패 처리
            override fun onFailure(call: Call<FishName>, t: Throwable) {
                Log.d("Error", ""+t.toString())
            }
            //api 요청 성공 처리
            override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                var result: FishName? = response.body()
                if(java.util.Random().nextInt(10) + 1 == 1)
                else{
                    var query = "INSERT INTO animals('num','name','price','image') values('${numPosition}','${result?.name?.KRko}','${result?.price}','${result?.image}')"
                    database.execSQL(query)
                    datas.apply {
                        add(DateClassTest("${result?.name?.KRko}","${result?.price}","${result?.image}"))
                    }
                }
            }
        })
    }

    val asyncTask = object : AsyncTask<Void, Int, MutableList<DateClassTest>>() {

        var numCheck = 1

        @SuppressLint("Range")
        override fun doInBackground(vararg params: Void?): MutableList<DateClassTest> {
            var query = "SELECT * FROM animals order by num;"
            var cursor = database.rawQuery(query, null)
            while(cursor.moveToNext()){
                var num = cursor.getInt(cursor.getColumnIndex("num"))
                var name = cursor.getString(cursor.getColumnIndex("name"))
                var price = cursor.getString(cursor.getColumnIndex("price"))
                var image = cursor.getString(cursor.getColumnIndex("image"))

                for (i in numCheck until num) { api(i) }

                numCheck = num + 1

                datas.add(DateClassTest("$name","$price","$image"))
            }

            if (numCheck != 81 && datas.size != 0) {
                for (i in numCheck..80) { api(i) }
            }

            if (datas.size == 0) {
                for (i in 1..80) api(i)
            }

            return datas
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: MutableList<DateClassTest>) {
            super.onPostExecute(result)
            setRecyclerView(result)
        }
    }

    fun setRecyclerView(result : MutableList<DateClassTest>) {
        addDataToRecyclerView(result)
    }

    private fun addDataToRecyclerView(result : MutableList<DateClassTest>) {
        result?.let {
            fishAdapter.datas = it
        }
        fishAdapter.notifyDataSetChanged()
    }
}