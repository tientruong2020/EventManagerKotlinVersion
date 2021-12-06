package com.example.myapplication.Data.Remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.Comment
import com.example.myapplication.model.Event
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class RTDBRepositoryImpl : RTDBRepository {

    companion object {
        const val BUCKET_EVENT_IMAGES_KEY = "Event Image"
        const val EVENT_TBL_NAME = "Events"
        const val USERS_TBL_NAME = "Users"
        const val LIKES_TBL_NAME = "Likes"
        const val COMMENTS_TBL_NAME = "Comments"
    }

    private var firebaseAuth: FirebaseAuth
    private val database: DatabaseReference
    private val eventRef: DatabaseReference
    private val userRef: DatabaseReference
    private val storageReference: StorageReference
    private val likeRef: DatabaseReference
    private val commentRef: DatabaseReference

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        storageReference = FirebaseStorage.getInstance().reference.child(BUCKET_EVENT_IMAGES_KEY)
        userRef = database.child(USERS_TBL_NAME)
        eventRef = database.child(EVENT_TBL_NAME)
        likeRef = database.child(LIKES_TBL_NAME)
        commentRef = database.child(COMMENTS_TBL_NAME)
    }

    override fun uploadEventImages(eventImagesUri: ArrayList<Uri>): MutableLiveData<ArrayList<String>> {
        val imageUrlListLiveData = MutableLiveData<ArrayList<String>>()
        var eventImagesList = ArrayList<String>()
        val totalItem = eventImagesUri.size
        var count = 0

        for (i in 0..totalItem - 1) {
            val timestamp = System.currentTimeMillis().toString()
            val filename = "IMG_" + timestamp
            val fileUploader: StorageReference = storageReference.child(filename)
            fileUploader.putFile(eventImagesUri.get(i)).addOnSuccessListener {
                fileUploader.downloadUrl.addOnSuccessListener {
                    count++
                    Log.d("UploadEventImage", "Successfully upload Avatar: ${count}/${totalItem}")
                    eventImagesList.add(it.toString())
                    if (count == totalItem) {
                        imageUrlListLiveData.value = eventImagesList
                    }
                }
            }
        }
        return imageUrlListLiveData
    }

    override fun addEventToDB(event: Event): MutableLiveData<Boolean> {
        val resultLiveData = MutableLiveData<Boolean>()
        val key = eventRef.push().key
        if (key == null) {
            Log.w("EventToDB", "Couldn't get push key for posts")
        }
        val uid = firebaseAuth.currentUser?.uid
        val newEventRef = eventRef.child(key!!)
        newEventRef.setValue(event).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val childUpdates = hashMapOf<String, Any>(
                    "${uid}/Events/${key}" to key
                )
                userRef.updateChildren(childUpdates).addOnCompleteListener { taskU ->
                    resultLiveData.value = taskU.isSuccessful
                }
            }
        }
        return resultLiveData
    }

    override fun getAllEvent(): MutableLiveData<ArrayList<Event>> {
        val eventArrList = ArrayList<Event>()
        val eventListLiveData = MutableLiveData<ArrayList<Event>>()
        eventRef.orderByChild("createdAt").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (eventSnapshot in dataSnapshot.children) {
                    val eventName = eventSnapshot.child("eventName").value.toString()
                    val description = eventSnapshot.child("description").value.toString()
                    val endDate = eventSnapshot.child("endDate").value.toString()
                    val startDate = eventSnapshot.child("startDate").value.toString()
                    val placeOrUrl = eventSnapshot.child("placeOrUrl").value.toString()
                    val limit: Long = eventSnapshot.child("limit").value as Long
                    val uid = eventSnapshot.child("uid").value.toString()
                    val isOnline = eventSnapshot.child("online").value as Boolean
                    val createdAt = eventSnapshot.child("createdAt").value as Long
                    val imageArrList = ArrayList<String>()
                    val eventKey = eventSnapshot.key
                    eventRef.child(eventKey!!).child("imagesList")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(imgSnapshot: DataSnapshot) {
                                val totalImages = imgSnapshot.childrenCount
                                var countImg: Long = 0
                                for (imageUri in imgSnapshot.children) {
                                    imageArrList.add(imageUri.value.toString().trim())
                                    countImg++
                                    if (countImg == totalImages) {
                                        userRef.child(uid)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val user: User =
                                                        snapshot.getValue(User::class.java)!!
                                                    val eventInLike = likeRef.child(eventKey)
                                                    eventInLike.addValueEventListener(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val currentUserId =
                                                                firebaseAuth.currentUser?.uid
                                                            val isLiked =
                                                                snapshot.hasChild(currentUserId!!)
                                                            val event = Event(
                                                                eventKey,
                                                                uid,
                                                                user,
                                                                eventName,
                                                                description,
                                                                placeOrUrl,
                                                                endDate,
                                                                startDate,
                                                                isOnline,
                                                                limit,
                                                                createdAt,
                                                                imageArrList,
                                                                isLiked
                                                            )
                                                            if (eventArrList.size > 0) {
                                                                var count = 0
                                                                for (i in 0..eventArrList.size - 1) {
                                                                    count++
                                                                    if (eventArrList.get(i).id == event.id) {
                                                                        eventArrList.set(i, event)
                                                                        break
                                                                    }
                                                                    if (count == eventArrList.size) {
                                                                        eventArrList.add(event)
                                                                    }
                                                                }
                                                            } else {
                                                                eventArrList.add(event)
                                                            }
                                                            eventListLiveData.value = eventArrList
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            TODO("Not yet implemented")
                                                        }

                                                    })
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Log.d(
                                                        "GetAllEvent",
                                                        error.toException().toString()
                                                    )
                                                }

                                            })

                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("GetAllEvent", error.toException().toString())
                            }

                        })

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("GetAllEvent", error.toException().toString())
            }
        })
        return eventListLiveData
    }

    override fun clickLikeOrDislikeEvent(eventId: String): MutableLiveData<Boolean> {
        val uid = firebaseAuth.currentUser?.uid!!
        val eventInLikes = likeRef.child(eventId)
        val result = MutableLiveData<Boolean>()
        var alreadyLiked = true
        eventInLikes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (alreadyLiked) {
                    if (snapshot.hasChild(uid)) {
                        eventInLikes.child(uid).removeValue()
                    } else {
                        val childUpdates = hashMapOf<String, Any>(
                            uid to uid
                        )
                        eventInLikes.updateChildren(childUpdates).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("ClickLikeEvent", "$uid like $eventId")
                            } else {
                                Log.e("ClickLikeEvent", "Error")
                            }
                        }
                    }
                    alreadyLiked = false
                    result.value = !alreadyLiked
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override suspend fun joinEvent(eventId: String): MutableLiveData<Boolean> {
        val uid = this.firebaseAuth.currentUser!!.uid
        val result = MutableLiveData<Boolean>()
        var alreadyJoin = true
        eventRef.child(eventId).child("Joiner").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (alreadyJoin){
                    if(snapshot.hasChild(uid)){
                        eventRef.child(eventId).child("Joiner").child(uid).removeValue()
                        userRef.child(uid).child("JoinedEvents").child(eventId).removeValue()
                    }
                    else{
                        val joinedEventData = hashMapOf<String, Any>(
                            eventId to eventId
                        )
                        val joinerData = hashMapOf<String, Any>(
                            uid to uid
                        )
                        eventRef.child(eventId).child("Joiner").updateChildren(joinerData)
                        userRef.child(uid).child("JoinedEvents").updateChildren(joinedEventData)
                    }
                }
                alreadyJoin = false
                result.value = !alreadyJoin
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override suspend fun addEventComment(
        content: String,
        eventId: String
    ): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val uid = firebaseAuth.currentUser?.uid
        val createdAt = System.currentTimeMillis()
        val commentInEventRef = commentRef.child(eventId)
        val commentKey = commentInEventRef.push().key
        val comment = Comment(uid!!, content, createdAt)
        commentInEventRef.child(commentKey!!).setValue(comment).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = true
            } else {
                Log.d("add_comment", "fail")
            }
        }
        return result
    }

    override fun getAllCommentByEventId(eventId: String): MutableLiveData<ArrayList<Comment>> {
        val commentsLiveData = MutableLiveData<ArrayList<Comment>>()
        val commentArrList = ArrayList<Comment>()
        commentRef.child(eventId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("commentSize",dataSnapshot.childrenCount.toString())
                if (dataSnapshot.exists()) {
                    for (commentSnapshot in dataSnapshot.children) {
                        val creatorId = commentSnapshot.child("uid").value.toString()
                        val content = commentSnapshot.child("content").value.toString()
                        val createdAt = commentSnapshot.child("createdAt").value as Long
                        val commentKey = commentSnapshot.key
                        userRef.child(creatorId).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user: User? = snapshot.getValue(User::class.java)
                                if (user != null) {
                                    val comment =
                                        Comment(commentKey.toString(), user, content, createdAt)
                                    if(commentArrList.size > 0){
                                        var count =0
                                        for (i in 0.. commentArrList.size-1){
                                            count++
                                            if (commentArrList[i].id == comment.id){
                                                commentArrList.set(i, comment)
                                                break
                                            }
                                            if (count == commentArrList.size){
                                                commentArrList.add(comment)
                                            }
                                        }
                                    }else{
                                        commentArrList.add(comment)
                                    }
                                    commentsLiveData.value = commentArrList
                                }
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

        return commentsLiveData
    }

    override fun getEventById(eventId: String): MutableLiveData<Event> {
        val eventLiveData = MutableLiveData<Event>()
        eventRef.child(eventId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    val eventName = snapshot.child("eventName").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val endDate = snapshot.child("endDate").value.toString()
                    val startDate = snapshot.child("startDate").value.toString()
                    val placeOrUrl = snapshot.child("placeOrUrl").value.toString()
                    val limit: Long = snapshot.child("limit").value as Long
                    val uid = snapshot.child("uid").value.toString()
                    val isOnline = snapshot.child("online").value as Boolean
                    val createdAt = snapshot.child("createdAt").value as Long
                    val imageArrList = ArrayList<String>()
                    eventRef.child(eventId).child("imagesList").addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(imgSnapshot: DataSnapshot) {
                            val totalImages = imgSnapshot.childrenCount
                            var countImg: Long = 0
                            for (imageUri in imgSnapshot.children) {
                                imageArrList.add(imageUri.value.toString().trim())
                                countImg++
                                if (countImg == totalImages){
                                    userRef.child(uid).addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val user: User = snapshot.getValue(User::class.java)!!
                                            val eventInLike = likeRef.child(eventId)
                                            eventInLike.addValueEventListener(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val currentUserId =
                                                        firebaseAuth.currentUser?.uid
                                                    val isLiked =
                                                        snapshot.hasChild(currentUserId!!)
                                                    val event = Event(eventId,uid, user, eventName, description,placeOrUrl,endDate,startDate,isOnline,limit,createdAt,imageArrList,isLiked)
                                                    eventLiveData.value = event
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })
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
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return eventLiveData
    }

    override fun isExistEvent(eventId: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        eventRef.child(eventId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                result.value = snapshot.hasChildren()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override fun getEventsByUid(currentUid:String): MutableLiveData<ArrayList<Event>> {
        val eventArrList = ArrayList<Event>()
        val myEventLiveData = MutableLiveData<ArrayList<Event>>()

        eventRef.orderByChild("uid").equalTo(currentUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    for(eventSnapshot in snapshot.children){
                        val eventName = eventSnapshot.child("eventName").value.toString()
                        val description = eventSnapshot.child("description").value.toString()
                        val endDate = eventSnapshot.child("endDate").value.toString()
                        val startDate = eventSnapshot.child("startDate").value.toString()
                        val placeOrUrl = eventSnapshot.child("placeOrUrl").value.toString()
                        val limit: Long = eventSnapshot.child("limit").value as Long
                        val uid = eventSnapshot.child("uid").value.toString()
                        val isOnline = eventSnapshot.child("online").value as Boolean
                        val createdAt = eventSnapshot.child("createdAt").value as Long
                        val imageArrList = ArrayList<String>()
                        val eventId = eventSnapshot.key!!
                        eventRef.child(eventId).child("imagesList").addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(imgSnapshot: DataSnapshot) {
                                val totalImages = imgSnapshot.childrenCount
                                var countImg: Long = 0
                                for (imageUri in imgSnapshot.children) {
                                    imageArrList.add(imageUri.value.toString().trim())
                                    countImg++
                                    if (countImg == totalImages){
                                        val event = Event(eventId,uid, eventName, description,placeOrUrl,endDate,startDate,isOnline,limit,createdAt,imageArrList)
                                        eventArrList.add(event)
                                        myEventLiveData.value = eventArrList
                                    }
                                }
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

        return myEventLiveData
    }

    override fun getCountEventsByUid(currentUid: String): MutableLiveData<Long> {
        val result = MutableLiveData<Long>()
        eventRef.orderByChild("Uid").equalTo(currentUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    result.value = snapshot.childrenCount
                    Log.d("EventNumber", snapshot.childrenCount.toString())
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

    override fun searchEvent(eventName: String): MutableLiveData<ArrayList<Event>> {
        val result = MutableLiveData<ArrayList<Event>>()
        val eventList = ArrayList<Event>()
        eventRef.orderByChild("eventName").startAt(eventName).endAt(eventName +"\uf8ff").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren()){
                    Log.d("eventcount", snapshot.childrenCount.toString())
                    for(eventSnapshot in snapshot.children){
                        val eventName = eventSnapshot.child("eventName").value.toString()
                        val description = eventSnapshot.child("description").value.toString()
                        val endDate = eventSnapshot.child("endDate").value.toString()
                        val startDate = eventSnapshot.child("startDate").value.toString()
                        val placeOrUrl = eventSnapshot.child("placeOrUrl").value.toString()
                        val limit: Long = eventSnapshot.child("limit").value as Long
                        val uid = eventSnapshot.child("uid").value.toString()
                        val isOnline = eventSnapshot.child("online").value as Boolean
                        val createdAt = eventSnapshot.child("createdAt").value as Long
                        val imageArrList = ArrayList<String>()
                        val eventId = eventSnapshot.key!!
                        eventRef.child(eventId).child("imagesList").addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(imgSnapshot: DataSnapshot) {
                                val totalImages = imgSnapshot.childrenCount
                                var countImg: Long = 0
                                for (imageUri in imgSnapshot.children) {
                                    imageArrList.add(imageUri.value.toString().trim())
                                    countImg++
                                    if (countImg == totalImages){
                                        val event = Event(eventId,uid, eventName, description,placeOrUrl,endDate,startDate,isOnline,limit,createdAt,imageArrList)
                                        eventList.add(event)
                                        result.value = eventList
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                }
                else{
                    result.value = eventList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return result
    }

    override fun getEventByDate(date: String): MutableLiveData<ArrayList<Event>> {
        val simpleDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val uid = this.firebaseAuth.currentUser!!.uid
        val result = MutableLiveData<ArrayList<Event>>()
        val eventArrayList = ArrayList<Event>()
        val beginDay = simpleDate.parse(date +" 00:00")
        val endDay = simpleDate.parse(date+ " 23:59")
        eventRef.orderByChild("uid").equalTo(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren()){

                    for(eventSnapshot in snapshot.children){
                        val eventName = eventSnapshot.child("eventName").value.toString()
                        val description = eventSnapshot.child("description").value.toString()
                        val endDate = eventSnapshot.child("endDate").value.toString()
                        val startDate = eventSnapshot.child("startDate").value.toString()
                        val placeOrUrl = eventSnapshot.child("placeOrUrl").value.toString()
                        val limit: Long = eventSnapshot.child("limit").value as Long
                        val uid = eventSnapshot.child("uid").value.toString()
                        val isOnline = eventSnapshot.child("online").value as Boolean
                        val createdAt = eventSnapshot.child("createdAt").value as Long
                        val imageArrList = ArrayList<String>()
                        val eventId = eventSnapshot.key!!
                        val start_date = simpleDate.parse(startDate)
                        val end_date = simpleDate.parse(endDate)

                        if (start_date.before(endDay) && end_date.after(beginDay)){
                            eventRef.child(eventId).child("imagesList").addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(imgSnapshot: DataSnapshot) {
                                    val totalImages = imgSnapshot.childrenCount
                                    var countImg: Long = 0
                                    for (imageUri in imgSnapshot.children) {
                                        imageArrList.add(imageUri.value.toString().trim())
                                        countImg++
                                        if (countImg == totalImages){
                                            val event = Event(eventId,uid, eventName, description,placeOrUrl,endDate,startDate,isOnline,limit,createdAt,imageArrList)
                                            eventArrayList.add(event)
                                            result.value = eventArrayList
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }

                    }
                }
                else{
                    result.value = eventArrayList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return  result
    }

    override fun getJoinedEventByDate(date:String):MutableLiveData<ArrayList<Event>>{
        val uid = this.firebaseAuth.currentUser?.uid
        val result = MutableLiveData<ArrayList<Event>>()
        val eventArrList = ArrayList<Event>()
        val simpleDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val beginDay = simpleDate.parse(date +" 00:00")
        val endDay = simpleDate.parse(date+ " 23:59")
        userRef.child(uid!!).child("JoinedEvents").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    Log.d("JoinedEventsCount", snapshot.childrenCount.toString()
                    )
                    for(eventIdSnapshot in snapshot.children){
                        val eventId = eventIdSnapshot.value
                        eventRef.child(eventId.toString()).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(eventSnapshot: DataSnapshot) {
                                val eventName = eventSnapshot.child("eventName").value.toString()
                                val description = eventSnapshot.child("description").value.toString()
                                val endDate = eventSnapshot.child("endDate").value.toString()
                                val startDate = eventSnapshot.child("startDate").value.toString()
                                val placeOrUrl = eventSnapshot.child("placeOrUrl").value.toString()
                                val limit: Long = eventSnapshot.child("limit").value as Long
                                val uid = eventSnapshot.child("uid").value.toString()
                                val isOnline = eventSnapshot.child("online").value as Boolean
                                val createdAt = eventSnapshot.child("createdAt").value as Long
                                val imageArrList = ArrayList<String>()
                                val eventId = eventSnapshot.key!!
                                val start_date = simpleDate.parse(startDate)
                                val end_date = simpleDate.parse(endDate)
                                Log.d("EndDate",endDate)
                                if (start_date.before(endDay) && end_date.after(beginDay)){
                                    eventRef.child(eventId).child("imagesList").addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(imgSnapshot: DataSnapshot) {
                                            val totalImages = imgSnapshot.childrenCount
                                            var countImg: Long = 0
                                            for (imageUri in imgSnapshot.children) {
                                                imageArrList.add(imageUri.value.toString().trim())
                                                countImg++
                                                if (countImg == totalImages){
                                                    val event = Event(eventId,uid, eventName, description,placeOrUrl,endDate,startDate,isOnline,limit,createdAt,imageArrList)
                                                    eventArrList.add(event)
                                                    result.value = eventArrList
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                }
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
        return  result
    }


}