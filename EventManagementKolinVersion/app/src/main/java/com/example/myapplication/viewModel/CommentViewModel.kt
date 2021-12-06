package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Data.Remote.RTDBRepositoryImpl
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import com.example.myapplication.model.Comment
import com.example.myapplication.model.User
import kotlinx.coroutines.launch

class CommentViewModel(application: Application): AndroidViewModel(application) {

    val userRepositoryImpl: UserRepositoryImpl
    val rtdbRepositoryImpl: RTDBRepositoryImpl
    var resultAddedComment: MutableLiveData<Boolean>? = null

    init {
        userRepositoryImpl = UserRepositoryImpl(application)
        rtdbRepositoryImpl = RTDBRepositoryImpl()
    }

    fun getCurentUser():MutableLiveData<User>{
        return userRepositoryImpl.getCurrentUserByID()
    }

    fun addComment(content:String, eventId:String){
        viewModelScope.launch {
            resultAddedComment = rtdbRepositoryImpl.addEventComment(content,eventId)
        }
    }

    fun getAllComment(eventId: String):MutableLiveData<ArrayList<Comment>>{
        return  rtdbRepositoryImpl.getAllCommentByEventId(eventId)
    }
    fun getCurrentUid():String{
        return userRepositoryImpl.getCurrentUserId()
    }
}