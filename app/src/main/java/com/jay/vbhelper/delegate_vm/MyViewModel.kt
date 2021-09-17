package com.jay.vbhelper.delegate_vm

import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/17
 */
class MyViewModel : ViewModel() {




    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also { loadUsers() }
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}