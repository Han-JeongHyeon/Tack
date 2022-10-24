package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val retrofit : Retrofit by inject()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(application,retrofit)
        )[MainViewModel::class.java]
    }

    private var fishAdapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fishAdapter = Adapter()

        viewModel.insertFishList()

        binding.Recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.roomInput.value == "") {
                    if (!binding.Recycler.canScrollVertically(1)) {
                        viewModel.insertFishList()
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

        binding.Recycler.adapter = fishAdapter

        fishAdapter = Adapter().apply {
//            setHasStableIds(true)
            setOnItemClickListener(object : Adapter.OnItemClickListener { // 이벤트 리스너
                override fun onItemClick(v: View, item: Fish) {
                    viewModel.favorite(v, item)
                }
                override fun onItemLongClick(v: View, item: Fish) {
                }
            })
        }

        binding.Recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fishAdapter
        }
    }

    private fun setObserver() {
        viewModel.selectList.observe(this) {
            fishAdapter!!.submitList(it)
        }

        viewModel.selectListObserver.observe(this){
            if (viewModel.roomInput.value == "") {
                viewModel.getFishList()
            }
        }
    }

}