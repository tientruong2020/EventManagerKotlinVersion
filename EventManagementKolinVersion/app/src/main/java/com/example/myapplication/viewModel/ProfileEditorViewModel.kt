package com.example.myapplication.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import com.example.myapplication.model.User

class ProfileEditorViewModel(application: Application): AndroidViewModel(application) {

    private var userRepositoryImpl: UserRepositoryImpl

    init {
        userRepositoryImpl = UserRepositoryImpl(application)
    }

    fun updateUserInfo(user: User, localImgUri: Uri):MutableLiveData<Boolean>{
        return  userRepositoryImpl.updateUserInfo(user, localImgUri)
    }

    fun updateUserWithoutAvatar(user: User):MutableLiveData<Boolean>{
        return  userRepositoryImpl.updateUserWithoutAvatar(user)
    }
}