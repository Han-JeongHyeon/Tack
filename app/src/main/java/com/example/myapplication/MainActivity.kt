package com.example.myapplication

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    //뷰 바인딩
    private lateinit var binding: ActivityMainBinding
    // SQLite
    // Room db
    // CamelCase
    //어뎁터 연결
    lateinit var fishAdapter: Adapter
    val datas = mutableListOf<DateClassTest>()
//    val datas = SparseArray<DateClassTest>()
    // SnakeStyle
    //SparseArray
//    var nameArray : SparseArray<DateClassTest>? = null

    lateinit var dbHelper: SqliteHelper
    lateinit var database: SQLiteDatabase

    var numCheckList =  ArrayList<String>()
    var numList =  ArrayList<String>()

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


//        var query = "delete from animals;"
//        database.execSQL(query)

//        for (i in 0 until 80) {
//            arr?.append(i, "${i + 1}")
//            Log.d("TAG", "asdasdasdsdaaa")
//        }

        asyncTask.execute()

    }

    fun apiCallBack(callback : (MutableList<DateClassTest>) -> Unit){
        val retrofit = Retrofit.Builder().baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build();
        val service = retrofit.create(FishName_interface::class.java);

        var j = 0

//        reqcnt=0
        if (datas.size == 0) {
            for (i in 1..80) {
                service.getName("${i}").enqueue(object: Callback<FishName>{
                    //api 요청 실패 처리
                    override fun onFailure(call: Call<FishName>, t: Throwable) {
                        Log.d("Error", ""+t.toString())
                    }
                    //api 요청 성공 처리
                    override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                        var result: FishName? = response.body()
                        //Array에 값 저장
                        if(i % 2 == 0){
                            j++
                        }
                        else{
                            var query = "INSERT INTO animals('num','name','price','image') values('${i}','${result?.name?.KRko}','${result?.price}','${result?.image}');"
                            database.execSQL(query)
                            datas.apply {
                                add(DateClassTest("${result?.name?.KRko}","${result?.price}","${result?.image}"))
                            }
                        }
                        if (datas.size+j == 80) {
                            callback(datas!!)
                        }
                    }
                })
            }
        }
        else{
            Log.d("TAG", "2132132 ${numCheckList.size}")
            for (i in 0 until numCheckList.size) {
                service.getName("${numCheckList.get(i)}").enqueue(object: Callback<FishName>{
                    //api 요청 실패 처리
                    override fun onFailure(call: Call<FishName>, t: Throwable) {
                        Log.d("Error", ""+t.toString())
                    }
                    //api 요청 성공 처리
                    override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                        var result: FishName? = response.body()
                            var query = "INSERT INTO animals('num','name','price','image') values('${numCheckList.get(i)}','${result?.name?.KRko}','${result?.price}','${result?.image}');"
                            database.execSQL(query)
                            datas.apply {
                                add(DateClassTest("${result?.name?.KRko}","${result?.price}","${result?.image}"))
                            }
                        if (datas.size == 80) {
                            callback(datas!!)
                        }
                    }
                })
            }
        }

    }

    val asyncTask = object : AsyncTask<Void, Int, MutableList<DateClassTest>>() {

        var numCheck = 1

        @SuppressLint("Range")
        override fun doInBackground(vararg params: Void?): MutableList<DateClassTest> {
            Log.d("TAG", "doInBackground1")
            var query = "SELECT * FROM animals order by num;"
            var cursor = database.rawQuery(query, null)
            while(cursor.moveToNext()){
                var num = cursor.getInt(cursor.getColumnIndex("num"))
                var name = cursor.getString(cursor.getColumnIndex("name"))
                var price = cursor.getString(cursor.getColumnIndex("price"))
                var image = cursor.getString(cursor.getColumnIndex("image"))
                datas.add(DateClassTest("${name}","${price}","${image}"))

//                numCheckList.add("$num")

                forEnd@ for (i in numCheck..80) {
                    if(num == i){
                        numCheck = num + 1
                        break@forEnd
                    }
                    else{
                        numCheckList.add("$i")
                    }
                    if(datas.size == 40 && i != 80){
                        numCheckList.add("80")
                    }
                }
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
        if (result.size == 0){
           apiCallBack {
               addDataToRecyclerView(it)
           }
        }
        else if (result.size <= 79){
            addDataToRecyclerView(result)
            apiCallBack {
                addDataToRecyclerView(it)
            }
        }
        else{
            addDataToRecyclerView(result)
        }
    }

    private fun addDataToRecyclerView(result : MutableList<DateClassTest>) {
        result?.let {
            fishAdapter.datas = it
        }
        fishAdapter.notifyDataSetChanged()
    }
}
