package com.example.myapplication.data.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FollowRepositoyImpl: FollowRepository {

    companion object{
        const val FLOW_TBL_NAME = "Follow"
        const val FLOWER_TBL_NAME = "Follower"
        const val FLOWING_TBL_NAME = "Following"
        const val USER_TBL_NAME = "Users"
    }

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference
    private val flowRef :DatabaseReference
    private val userRef :DatabaseReference

    init {
        database = Firebase.database.reference
        flowRef = database.child(FLOW_TBL_NAME)
        userRef = database.child(USER_TBL_NAME)
    }

    private fun getCurrentUserId(): String {
        return firebaseAuth.currentUser!!.uid
    }

    override suspend fun flowUser(userId: String):MutableLiveData<Boolean> {
        val currentUid = getCurrentUserId()
        var alreadyFlow = true
        val result = MutableLiveData<Boolean>()
        flowRef.child(currentUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (alreadyFlow){
                    if (snapshot.child(FLOWING_TBL_NAME).hasChild(userId)){
                        Log.d("Following","exist")
                        flowRef.child(currentUid).child(FLOWING_TBL_NAME).child(userId).removeValue()
                        flowRef.child(userId).child(FLOWER_TBL_NAME).child(currentUid).removeValue()
                    }
                    else{
                        Log.d("Following","notexist")
                        val flowingUpdates = hashMapOf<String, Any>(
                            userId to userId
                        )
                        val flowerUpdates = hashMapOf<String, Any>(
                            currentUid to currentUid
                        )
                        flowRef.child(currentUid).child(FLOWING_TBL_NAME).updateChildren(flowingUpdates)
                        flowRef.child(userId).child(FLOWER_TBL_NAME).updateChildren(flowerUpdates)
                    }
                }
                alreadyFlow = false
                result.value = !alreadyFlow
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override fun isFollwed(userId: String): MutableLiveData<Boolean> {
        val currentUid = getCurrentUserId()
        val result = MutableLiveData<Boolean>()
        flowRef.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                result.value = snapshot.child(FLOWER_TBL_NAME).hasChild(currentUid)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }


    override fun getFollowingUser(): MutableLiveData<ArrayList<User>> {
        val result = MutableLiveData<ArrayList<User>>()
        val userList = ArrayList<User>()
        val currentUid = firebaseAuth.currentUser!!.uid
        flowRef.child(currentUid).child(FLOWING_TBL_NAME).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren()){
                    for(userIdSnapshot in snapshot.children){
                        val userID = userIdSnapshot.value.toString()
                        userRef.child(userID).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(userSnapShot: DataSnapshot) {
                                val userName = userSnapShot.child("user_name").value.toString()
                                val bio = userSnapShot.child("bio").value.toString()
                                val email = userSnapShot.child("email").value.toString()
                                val avatar_url = userSnapShot.child("avatar_url").value.toString()
                                val user = User(userID,userName,email,avatar_url,bio)
                                userList.add(user)
                                result.value = userList
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override fun countFollower(userId: String): MutableLiveData<Long> {
        val result = MutableLiveData<Long>()
        flowRef.child(userId).child(FLOWER_TBL_NAME).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    result.value = snapshot.childrenCount
                }
                else{
                    result.value = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override fun countFollowing(userId: String): MutableLiveData<Long> {
        val result = MutableLiveData<Long>()
        flowRef.child(userId).child(FLOWING_TBL_NAME).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    result.value = snapshot.childrenCount
                }
                else{
                    result.value = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }
}