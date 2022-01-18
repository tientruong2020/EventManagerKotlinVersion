package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.RTDBRepositoryImpl
import com.example.myapplication.data.remote.UserRepositoryImpl
import com.example.myapplication.model.Event
import com.example.myapplication.model.User
import kotlinx.coroutines.launch

class HomeViewModel(application: Application):AndroidViewModel(application) {

    val rtdbRepositoryImpl: RTDBRepositoryImpl
    val userRepositoryImpl: UserRepositoryImpl
    var resultOfJoinEvent: MutableLiveData<Boolean>? = null

    init {
        rtdbRepositoryImpl = RTDBRepositoryImpl()
        userRepositoryImpl = UserRepositoryImpl(application)
    }

    fun getAllEvent():MutableLiveData<ArrayList<Event>>{
        return rtdbRepositoryImpl.getAllEvent()
    }

    fun handleLikeOrDislikeEvent(eventId:String):MutableLiveData<Boolean>{
          return  rtdbRepositoryImpl.clickLikeOrDislikeEvent(eventId)
    }

    fun getUserInfo():MutableLiveData<User>{
        return userRepositoryImpl.getCurrentUserByID()
    }


    fun getCurrentUid():String{
        return userRepositoryImpl.getCurrentUserId()
    }

    fun joinEvent(eventId:String){
        viewModelScope.launch() {
            resultOfJoinEvent = rtdbRepositoryImpl.joinEvent(eventId)
        }
    }

}