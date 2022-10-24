package com.example.myapplication

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

var appModule = module {
    single { AppDatabase }
    single { RetrofitObject( get() ) }
    single { Repository( get(), get() ) }
    viewModel {
        MainViewModel( get() )
    }
}