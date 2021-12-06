package com.example.myapplication.Data.Remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    fun registerWithPassword(userName:String, email:String, password:String): MutableLiveData<Boolean>
    fun login(email: String, password: String): MutableLiveData<Boolean>
    fun getCurrentUserId():String
    fun getUserById(userId: String):MutableLiveData<User>
    fun changePassword(currentPW: String, newPW: String):MutableLiveData<Boolean>
    fun getCurrentUserByID():MutableLiveData<User>
    fun writeUserInRTDB(user: User, userId:String)
    fun isLogined():Boolean
    fun signout()
    fun updateUserInfo(user:User, localAvatarUri:Uri):MutableLiveData<Boolean>
    fun uploadAvatar(localUri:Uri): Uri?
    fun updateUserWithoutAvatar(user: User):MutableLiveData<Boolean>
    fun searchUser(username:String):MutableLiveData<ArrayList<User>>

}