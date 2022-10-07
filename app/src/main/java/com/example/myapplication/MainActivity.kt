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

        dbHelper = SqliteHelper(this, "fishName.db", null, 1)
        database = dbHelper.writableDatabase

        nameArray = SparseArray<DateClassTest>()

//        var query = "DELETE FROM animals;"
//        database.execSQL(query)

        asyncTask.execute()

    }

    val asyncTask = object : AsyncTask<Void, Int, String>() {
        @SuppressLint("Range")
        override fun doInBackground(vararg params: Void?): String {
            val retrofit = Retrofit.Builder().baseUrl("https://acnhapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create()).build();
            val service = retrofit.create(FishName_interface::class.java);

            CoroutineScope(Dispatchers.IO).launch {
                var query = "SELECT * FROM animals ORDER BY num asc;"
                var cursor = database.rawQuery(query, null)
                if(cursor.count == 0){
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
                                var query = "INSERT INTO animals('num','name','price','image') values('${i}','${result?.name?.KRko}','${result?.price}','${result?.image}');"
                                database.execSQL(query)
                                nameArray?.append(i,DateClassTest("${result?.name?.KRko}","${result?.price}","${result?.image}"))
                                if (nameArray?.size() == 80) {
                                    addDataToRecyclerView()
                                }
                            }
                        })
                    }
                }
                else{
                    while(cursor.moveToNext()){
                        var num = cursor.getString(cursor.getColumnIndex("num"))
                        var name = cursor.getString(cursor.getColumnIndex("name"))
                        var price = cursor.getString(cursor.getColumnIndex("price"))
                        var image = cursor.getString(cursor.getColumnIndex("image"))
                        nameArray?.append(num.toInt(),DateClassTest(name,price,image))
                        if (nameArray?.size() == 80) {
                            addDataToRecyclerView()
                        }
                    }
                }
            }

            return "완료"
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            binding.text.text = "${values[0]!!}"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            nameArray?.let {
                fishAdapter.datas = it
                fishAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun addDataToRecyclerView() {
        nameArray?.let {
            fishAdapter.datas = it
        }
        fishAdapter.notifyDataSetChanged()
    }
}
