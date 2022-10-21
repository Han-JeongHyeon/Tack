package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(application)
        )[MainViewModel::class.java]
    }

    private var fishAdapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fishAdapter = Adapter(application)

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

        binding.Recycler.adapter = fishAdapter

        fishAdapter = Adapter(application).apply {
//            setHasStableIds(true)
            setOnItemClickListener(object : Adapter.OnItemClickListener { // 이벤트 리스너
                override fun onItemClick(v: View, item: Fish) {
                    viewModel.favorite(v, item)
                }
//                override fun onItemLongClick(v: View, item: Fish) {
//                }
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