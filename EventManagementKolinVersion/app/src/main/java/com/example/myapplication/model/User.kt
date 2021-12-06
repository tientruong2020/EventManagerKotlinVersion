package com.example.myapplication.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class User: Serializable{
    var user_name:String = ""
    var email:String = ""
    var avatar_url : String = ""
    var bio : String = ""
    var uid: String? = null

    constructor(){}

    constructor(user_name:String, email:String, avatar_url:String, bio: String){
        this.user_name = user_name
        this.email = email
        this.avatar_url = avatar_url
        this.bio = bio
    }
    constructor(uid:String,user_name:String, email:String, avatar_url:String, bio: String){
        this.uid = uid
        this.user_name = user_name
        this.email = email
        this.avatar_url = avatar_url
        this.bio = bio
    }



    @Exclude
    fun toMap():Map<String, Any>{
        return mapOf(
            "user_name"  to user_name,
            "email" to email,
            "avatar_url" to avatar_url
        )
    }
}