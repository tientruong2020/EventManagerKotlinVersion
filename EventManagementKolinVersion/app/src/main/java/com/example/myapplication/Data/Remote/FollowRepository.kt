package com.example.myapplication.Data.Remote

import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.User

interface FollowRepository {

    suspend fun flowUser(userId:String):MutableLiveData<Boolean>
    fun isFollwed(userId: String):MutableLiveData<Boolean>
    fun getFollowingUser():MutableLiveData<ArrayList<User>>
    fun countFollower(userId: String):MutableLiveData<Long>
    fun countFollowing(userId: String):MutableLiveData<Long>
}