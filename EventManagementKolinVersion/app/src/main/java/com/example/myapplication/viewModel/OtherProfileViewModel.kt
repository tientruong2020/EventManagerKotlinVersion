package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Data.Remote.FollowRepository
import com.example.myapplication.Data.Remote.FollowRepositoyImpl
import com.example.myapplication.Data.Remote.RTDBRepositoryImpl
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import com.example.myapplication.model.Event
import com.example.myapplication.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtherProfileViewModel(application: Application): AndroidViewModel(application) {

    private val userRepositoryImpl : UserRepositoryImpl
    private val rtdbRepositoryImpl : RTDBRepositoryImpl
     val followRepositoyImpl: FollowRepository
    var resultHandleFollow  = MutableLiveData<Boolean>()

    init {
        userRepositoryImpl = UserRepositoryImpl(application)
        followRepositoyImpl = FollowRepositoyImpl()
        rtdbRepositoryImpl = RTDBRepositoryImpl()
    }

    fun getUserById(userId:String):MutableLiveData<User>{
        return userRepositoryImpl.getUserById(userId)
    }

    fun followOrUnfollow(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            resultHandleFollow = followRepositoyImpl.flowUser(userId)
        }
    }

    fun isFollowed(userId: String):MutableLiveData<Boolean>{
        return followRepositoyImpl.isFollwed(userId)
    }

    fun getEventsByUid(uid:String):MutableLiveData<ArrayList<Event>>{
        return rtdbRepositoryImpl.getEventsByUid(uid)
    }

    fun countEvents(Uid:String):MutableLiveData<Long>{
        return rtdbRepositoryImpl.getCountEventsByUid(Uid)
    }

    fun countFollower(Uid:String):MutableLiveData<Long>{
        return followRepositoyImpl.countFollower(Uid)
    }

    fun countFollowing(Uid: String):MutableLiveData<Long>{
        return followRepositoyImpl.countFollowing(Uid)
    }

}