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

    var strList =  ArrayList<String>();

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RecyclerView 연결
        fishAdapter = Adapter(this)
        binding.Recycler.adapter = fishAdapter

        dbHelper = SqliteHelper(this, "fishName.db", null, 1)
        database = dbHelper.writableDatabase

//        var query = "delete from animals;"
//        database.execSQL(query)

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
            for (i in 0 until strList.size) {
                service.getName("${strList.get(i)}").enqueue(object: Callback<FishName>{
                    //api 요청 실패 처리
                    override fun onFailure(call: Call<FishName>, t: Throwable) {
                        Log.d("Error", ""+t.toString())
                    }
                    //api 요청 성공 처리
                    override fun onResponse(call: Call<FishName>, response: Response<FishName>) {
                        var result: FishName? = response.body()
                            var query = "update animals set name = '${result?.name?.KRko}', price = '${result?.price}', image = '${result?.image}' where num = '${strList.get(i)}';"
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

        @SuppressLint("Range")
        override fun doInBackground(vararg params: Void?): MutableList<DateClassTest> {
            var query = "SELECT * FROM animals ORDER BY num asc;"
            var cursor = database.rawQuery(query, null)
            while(cursor.moveToNext()){
                var num = cursor.getString(cursor.getColumnIndex("num"))
                var name = cursor.getString(cursor.getColumnIndex("name"))
                var price = cursor.getString(cursor.getColumnIndex("price"))
                var image = cursor.getString(cursor.getColumnIndex("image"))
                if (name != "null") {
                    datas.add(DateClassTest("${name}","${price}","${image}"))
                }
                else{
                    strList.add("${num.toInt()-1}")
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
            apiCallBack {
                addDataToRecyclerView(it)
            }
        }
        else{
            Log.d("TAG", "setRecyclerView")
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
