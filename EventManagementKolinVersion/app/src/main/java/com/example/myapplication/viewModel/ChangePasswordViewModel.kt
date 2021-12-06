package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Data.Remote.UserRepositoryImpl

class ChangePasswordViewModel(application: Application):AndroidViewModel(application) {

    private var userRepositoryImpl: UserRepositoryImpl

    init {
        userRepositoryImpl = UserRepositoryImpl(application)
    }

    fun changePassword(currentPw:String, newPw:String):MutableLiveData<Boolean>{
        return  userRepositoryImpl.changePassword(currentPw,newPw)

    }

    fun signout(){
        userRepositoryImpl.signout()
    }
}