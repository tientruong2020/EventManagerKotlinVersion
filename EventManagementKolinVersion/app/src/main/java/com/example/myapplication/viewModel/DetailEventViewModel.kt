package com.example.myapplication.viewModel

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.RTDBRepositoryImpl
import com.example.myapplication.data.remote.UserRepositoryImpl
import com.example.myapplication.model.Comment
import com.example.myapplication.model.Event
import com.example.myapplication.model.User
import kotlinx.coroutines.launch

class DetailEventViewModel(application: Application): AndroidViewModel(application) {

    private val rtdbRepositoryImpl : RTDBRepositoryImpl
    private val userRepositoryImpl: UserRepositoryImpl
    var resultAddedComment: MutableLiveData<Boolean>? = null

    init {
        rtdbRepositoryImpl = RTDBRepositoryImpl()
        userRepositoryImpl = UserRepositoryImpl(application)
    }

    fun getEventById(eventId: String): MutableLiveData<Event> {
        return rtdbRepositoryImpl.getEventById(eventId)
    }

    fun getAllComment(eventId: String):MutableLiveData<ArrayList<Comment>>{
        return  rtdbRepositoryImpl.getAllCommentByEventId(eventId)
    }

    fun getCurrentUser():MutableLiveData<User>{
        return userRepositoryImpl.getCurrentUserByID()
    }

    fun addComment(content:String, eventId:String){
        viewModelScope.launch {
            resultAddedComment = rtdbRepositoryImpl.addEventComment(content,eventId)
        }
    }

    fun handleLikeOrDislikeEvent(eventId:String):MutableLiveData<Boolean>{
        return  rtdbRepositoryImpl.clickLikeOrDislikeEvent(eventId)
    }
    fun getCurrentUid():String{
        return userRepositoryImpl.getCurrentUserId()
    }
}