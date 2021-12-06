package com.example.myapplication.Data.Remote

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserRepositoryImpl(val application: Application):UserRepository {

    companion object{
        const val USERS_TBL = "Users"
        const val USER_AVATAR_BUCKET_NAME = "Avatar"
        const val UPLOAD_TAG = "upload_avatar"
    }
    private var firebaseAuth: FirebaseAuth
    private val database: DatabaseReference
    private val userRef: DatabaseReference
    private val storageReference: StorageReference
    var registerResult: MutableLiveData<Boolean>
    var userMutableLiveData: MutableLiveData<FirebaseUser>
    var loggedOutLiveData: MutableLiveData<Boolean>
    var userIdLiveData : MutableLiveData<String>
    var userInfoLiveData: MutableLiveData<User>
    init {
        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        storageReference = FirebaseStorage.getInstance().getReference().child(USER_AVATAR_BUCKET_NAME)
        userRef = database.child(USERS_TBL)
        userMutableLiveData = MutableLiveData<FirebaseUser>()
        loggedOutLiveData = MutableLiveData<Boolean>()
        userIdLiveData = MutableLiveData<String>()
        registerResult = MutableLiveData<Boolean>()
        userInfoLiveData = MutableLiveData<User>()
    }

    override fun registerWithPassword(userName:String,email: String, password: String):MutableLiveData<Boolean> {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener <AuthResult>(){ task ->
                if(task.isSuccessful){
                    val user = User(userName, email,"","")
                    val uid = task.result?.user?.uid
                    writeUserInRTDB(user, uid!!)
                    registerResult.postValue(true)
                }
                else{
                    Toast.makeText(application.applicationContext, "Email is existed", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return registerResult
    }


    override fun login(email: String, password: String): MutableLiveData<Boolean> {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful){
                    loggedOutLiveData.postValue(true)
                }
                else{
                    loggedOutLiveData.postValue(false)
                    Toast.makeText(application.applicationContext, "Email or Password is wrong!!", Toast.LENGTH_SHORT).show()
                }
            }
        return loggedOutLiveData
    }


    override fun getCurrentUserId():String {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.uid?: ""
    }

    override fun getUserById(userId: String): MutableLiveData<User> {
        val result = MutableLiveData<User>()
        userRef.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("user_name").value.toString()
                val bio = snapshot.child("bio").value.toString()
                val email = snapshot.child("email").value.toString()
                val avatar_url = snapshot.child("avatar_url").value.toString()
                val user = User(userName,email,avatar_url,bio)
                result.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override fun getCurrentUserByID(): MutableLiveData<User> {
        val uid = this.getCurrentUserId()
        if (uid != ""){
            val userRefById = userRef.child(uid)
            userRefById.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userInfoLiveData.value = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error", error.message)
                }

            })
        }
        return userInfoLiveData
    }
    override fun changePassword(currentPW: String, newPW: String): MutableLiveData<Boolean> {
        val currentUser = firebaseAuth.currentUser
        val result = MutableLiveData<Boolean>()
        if (currentUser != null) {
            val credential: AuthCredential =
                EmailAuthProvider.getCredential(currentUser.email!!, currentPW)
            currentUser.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser.updatePassword(newPW).addOnSuccessListener {
                        result.value = true
                    }
                }
                else{
                    result.value = false
                }
            }
        }
        return result
    }


    override fun writeUserInRTDB(user: User, userId:String) {
        userRef.child(userId).setValue(user)
    }

    override  fun isLogined(): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
    }

    override fun signout() {
        firebaseAuth.signOut()
    }

    override fun updateUserInfo(user: User,localAvatarUri:Uri): MutableLiveData<Boolean> {
        val resultLiveData = MutableLiveData<Boolean>()
        val uid = this.getCurrentUserId()
        val filename:String = this.getCurrentUserId()
        val fileUploader: StorageReference = storageReference.child(filename)
        fileUploader.putFile(localAvatarUri).addOnSuccessListener {
            Log.d(UPLOAD_TAG, "Successfully upload Avatar: ${it.metadata?.path}")
            fileUploader.downloadUrl.addOnSuccessListener {
                user.avatar_url = it.toString()
                val userIdRef = userRef.child(uid)
                userIdRef.setValue(user).addOnSuccessListener {
                    resultLiveData.value = true
                }
            }
        }

        return resultLiveData
    }

    override fun uploadAvatar(localUri: Uri): Uri? {
        var remoteUri :Uri? = null
        val filename:String = this.getCurrentUserId()
        val fileUploader: StorageReference = storageReference.child(filename)
        fileUploader.putFile(localUri).addOnSuccessListener {
            Log.d(UPLOAD_TAG, "Successfully upload Avatar: ${it.metadata?.path}")
            fileUploader.downloadUrl.addOnSuccessListener {
                remoteUri = it
            }
        }
        return remoteUri
    }

    override fun updateUserWithoutAvatar(user: User): MutableLiveData<Boolean> {
        val resultLiveData = MutableLiveData<Boolean>()
        val uid = this.getCurrentUserId()
        val userIdRef = userRef.child(uid)
        userIdRef.setValue(user).addOnSuccessListener {
            resultLiveData.value = true
        }.addOnFailureListener {
            resultLiveData.value = false
            Log.d("Updated_User", it.message.toString())
        }
        return resultLiveData
    }

    override fun searchUser(username: String): MutableLiveData<ArrayList<User>> {
        val result = MutableLiveData<ArrayList<User>>()
        val userList = ArrayList<User>()
        userRef.orderByChild("user_name").startAt(username).endAt(username +"\uf8ff").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    for (userSnapShot in snapshot.children){
                        val uid = userSnapShot.key.toString()
                        val userName = userSnapShot.child("user_name").value.toString()
                        val bio = userSnapShot.child("bio").value.toString()
                        val email = userSnapShot.child("email").value.toString()
                        val avatar_url = userSnapShot.child("avatar_url").value.toString()
                        val user = User(uid,userName,email,avatar_url,bio)
                        userList.add(user)
                        result.value = userList
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }


}