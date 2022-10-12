package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.text.LoginFilter
import android.util.Log
import java.sql.DatabaseMetaData

class AsyncTack(val context : Context, val page : Int, val pageSize : Int) : AsyncTask<Void, Int, MutableList<DateClassTest>>() {
    val datas = mutableListOf<DateClassTest>()
    lateinit var dbHelper: SqliteHelper
    lateinit var database: SQLiteDatabase

    @SuppressLint("Range")
    override fun doInBackground(vararg params: Void?): MutableList<DateClassTest> {
        dbHelper = SqliteHelper(context, "FishName.db", null, 1)
        database = dbHelper.writableDatabase

        var query = "SELECT * FROM animals order by num limit ${page * pageSize}, $pageSize;"
        var cursor = database.rawQuery(query, null)
        while(cursor.moveToNext()){
            var num = cursor.getInt(cursor.getColumnIndex("num"))
            var name = cursor.getString(cursor.getColumnIndex("name"))
            var price = cursor.getString(cursor.getColumnIndex("price"))
            var image = cursor.getString(cursor.getColumnIndex("image"))

            datas.add(DateClassTest(num, "$name","$price","$image"))
        }

        return datas
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: MutableList<DateClassTest>) {
        super.onPostExecute(result)
//        Log.d("TAG", "${result}")

    }

    override fun onCancelled(result: MutableList<DateClassTest>) {
        super.onCancelled(result)
    }

}