package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.remote.UserRepositoryImpl

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository: UserRepositoryImpl

    init {
        userRepository = UserRepositoryImpl(application)
    }

    fun doRegister(userName:String,email:String, password:String):MutableLiveData<Boolean>{
        return userRepository.registerWithPassword(userName, email,password)
    }
}