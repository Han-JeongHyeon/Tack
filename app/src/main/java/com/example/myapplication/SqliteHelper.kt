package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// SQLiteOpenHelper 상속받아 SQLite 를 사용하도록 하겠습니다.
class SqliteHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version){
    override fun onCreate(db: SQLiteDatabase) {
        var sql: String = "CREATE TABLE if not exists animals(" +
                "num int primary key," +
                "name text," +
                "price int," +
                "image text);"

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql: String = "DROP TABLE if exists animals"
        db.execSQL(sql)
        onCreate(db)
    }
}