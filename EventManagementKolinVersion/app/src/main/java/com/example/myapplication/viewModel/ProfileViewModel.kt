package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Data.Remote.FollowRepositoyImpl
import com.example.myapplication.Data.Remote.RTDBRepositoryImpl
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import com.example.myapplication.model.Event
import com.example.myapplication.model.User

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val userRepositoryImpl : UserRepositoryImpl
    private val rtdbRepositoryImpl : RTDBRepositoryImpl
    private val followRepositoyImpl: FollowRepositoyImpl

    init {
        userRepositoryImpl = UserRepositoryImpl(application)
        rtdbRepositoryImpl = RTDBRepositoryImpl()
        followRepositoyImpl = FollowRepositoyImpl()
    }

    fun getUserProfile():MutableLiveData<User>{
        return userRepositoryImpl.getCurrentUserByID()
    }

    fun getMyEvent(uid:String):MutableLiveData<ArrayList<Event>>{
        return rtdbRepositoryImpl.getEventsByUid(uid)
    }

    fun getFollowingUser():MutableLiveData<ArrayList<User>>{
        return followRepositoyImpl.getFollowingUser()
    }

    fun getCurrentUid():String{
        return userRepositoryImpl.getCurrentUserId()
    }

    fun countEvents(currentUid:String):MutableLiveData<Long>{
        return rtdbRepositoryImpl.getCountEventsByUid(currentUid)
    }

    fun countFollower(currentUid: String):MutableLiveData<Long>{
        return followRepositoyImpl.countFollower(currentUid)
    }

    fun countFollowing(currentUid: String):MutableLiveData<Long>{
        return followRepositoyImpl.countFollowing(currentUid)
    }
}