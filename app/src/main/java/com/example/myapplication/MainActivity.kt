package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

//상단에 내가 접속한 시간 나오기

class MainActivity : AppCompatActivity() {

    private val viewModel : MainViewModel by viewModel()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var fishAdapter: Adapter? = null

    var taskTimer: Timer? = null

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

    fun intent(v : View){
        val intent = Intent(this ,subActivity::class.java)
        startActivity(intent)
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

    override fun onStart() {
        taskTimer = viewModel.dateTimeToMillSec()
        super.onStart()
    }

    override fun onStop() {
        taskTimer!!.cancel()
        super.onStop()
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