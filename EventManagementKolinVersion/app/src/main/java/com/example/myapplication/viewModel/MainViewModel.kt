package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.remote.UserRepositoryImpl
import com.example.myapplication.model.User

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val userRepositoryImpl: UserRepositoryImpl

    init {
        userRepositoryImpl = UserRepositoryImpl(application)
    }


    fun isLogined(): Boolean {
        return userRepositoryImpl.isLogined()
    }

    fun getUserInfo():MutableLiveData<User>{
        return userRepositoryImpl.getCurrentUserByID()
    }

    fun signout(){
        userRepositoryImpl.signout()
    }
}