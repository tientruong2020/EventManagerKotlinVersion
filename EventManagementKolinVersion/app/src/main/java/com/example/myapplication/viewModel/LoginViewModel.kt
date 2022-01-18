package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.remote.UserRepositoryImpl

class LoginViewModel(application: Application):AndroidViewModel(application) {
    private val userRepository: UserRepositoryImpl
    val loginResult = MutableLiveData<Boolean>()
    init {
        userRepository = UserRepositoryImpl(application)

    }

    fun loginWithEmail(email:String, password:String):MutableLiveData<Boolean>{
        return userRepository.login(email,password)
    }
}