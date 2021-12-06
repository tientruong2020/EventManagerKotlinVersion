package com.example.myapplication.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Data.Remote.RTDBRepositoryImpl
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import com.example.myapplication.model.Event

class AdditionEventViewModel(application: Application): AndroidViewModel(application) {
    private var rtdbRepositoryImpl: RTDBRepositoryImpl

    private var userRepositoryImpl : UserRepositoryImpl
    init {
        rtdbRepositoryImpl = RTDBRepositoryImpl()
        userRepositoryImpl = UserRepositoryImpl(application)
    }

    fun addImgEvents(localImgList: ArrayList<Uri>):MutableLiveData<ArrayList<String>>{
        return rtdbRepositoryImpl.uploadEventImages(localImgList)
    }

    fun getUid(): String{
        return userRepositoryImpl.getCurrentUserId()
    }

    fun addEventToDB(event: Event):MutableLiveData<Boolean>{
        return rtdbRepositoryImpl.addEventToDB(event)
    }
}