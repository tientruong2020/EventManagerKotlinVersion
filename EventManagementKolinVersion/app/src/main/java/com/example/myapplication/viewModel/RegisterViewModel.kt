package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository: UserRepositoryImpl

    init {
        userRepository = UserRepositoryImpl(application)
    }

    fun doRegister(userName:String,email:String, password:String):MutableLiveData<Boolean>{
        return userRepository.registerWithPassword(userName, email,password)
    }
}