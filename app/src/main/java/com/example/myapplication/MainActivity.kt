package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(application)
        )[MainViewModel::class.java]
    }

    private var fishAdapter = Adapter()

    private lateinit var roomAdapter: Adapter

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.insertFishList(baseContext)

        binding.Recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.roomInput.value == "") {
                    if (!binding.Recycler.canScrollVertically(1)) {
                        viewModel.insertFishList(baseContext)
                    }
                }
            }
        })

        setView()
        setObserver()
    }

    private fun setView() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        roomAdapter = Adapter().apply {
            setHasStableIds(true)
        }

        binding.Recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomAdapter
        }

    }

    private fun setObserver() {
        binding.Recycler.adapter = fishAdapter

        viewModel.selectList.observe(this) {
            fishAdapter.submitList(it)
        }
    }

}