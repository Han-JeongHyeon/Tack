package com.example.myapplication

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class Retrofit(private val context : Context) {

    private fun getRetrofit(): Retrofit {

        val cache = Cache(File(context.cacheDir,"HTTP_Cache"),10 * 1024 * 1024L)

        val client = OkHttpClient.Builder()
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getRetrofitService(): Interface{
        return getRetrofit().create(Interface::class.java)
    }

}

class Repository(private val application : Application) {

    // Room Dao
    private val fishListDao = AppDatabase.getInstance(application)!!.getFishDao()

    // Use Room
    fun selectAll(): LiveData<List<Fish>> {
        return fishListDao.getAll()
    }

    fun selectFavorite(position: Int): Boolean{
        return fishListDao.selectFavorite(position)
    }

    fun selectPaging(pageSize : Int): List<Fish> {
        return fishListDao.getPage(pageSize)
    }

    suspend fun insertFishList(list : Fish) {
        fishListDao.insertAll(list)
    }

    suspend fun updateFavorite(list : Fish) {
        fishListDao.updateFavorite(list)
    }

    companion object {
        private var instance: Repository? = null

        fun getInstance(application : Application): Repository? {
            if (instance == null) instance = Repository(application)
            return instance
        }
    }
}

@Database(entities = [Fish::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFishDao(): Dao

    companion object {
        private const val DB_NAME = "fish"
        private var instance: AppDatabase? = null

        fun getInstance(application : Application): AppDatabase? { // singleton pattern
            if (instance == null) {
                synchronized(this){
                    instance = Room.databaseBuilder(application, AppDatabase::class.java, DB_NAME).build()
                }
            }
            return instance
        }
    }
}

class DiffUtil : DiffUtil.ItemCallback<Fish>() {
    override fun areItemsTheSame(
        oldItem: Fish,
        newItem: Fish
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Fish,
        newItem: Fish
    ): Boolean {
        return oldItem == newItem
    }
}

val appModule = module {
    single { Retrofit( get() ) }
    single { Repository( get() ) }
    single { AppDatabase }
}
