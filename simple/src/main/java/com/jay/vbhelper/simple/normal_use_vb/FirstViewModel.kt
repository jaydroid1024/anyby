package com.jay.vbhelper.simple.normal_use_vb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FirstViewModel : ViewModel() {

    private val data = MutableLiveData<ArrayList<String>>().apply {
        value = arrayListOf("Java", "Kotlin", "Dart", "Android", "KMM", "Flutter", "Jetpack")
    }
    val dataList: LiveData<ArrayList<String>> = data
}