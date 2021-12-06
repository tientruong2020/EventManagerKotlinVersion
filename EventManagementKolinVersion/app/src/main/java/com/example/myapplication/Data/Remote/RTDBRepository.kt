package com.example.myapplication.Data.Remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.Comment
import com.example.myapplication.model.Event
import com.example.myapplication.model.User
import java.util.*
import kotlin.collections.ArrayList

interface RTDBRepository {

    fun uploadEventImages(eventImagesUri: ArrayList<Uri>): MutableLiveData<ArrayList<String>>

    fun addEventToDB(event: Event): MutableLiveData<Boolean>

    fun getAllEvent(): MutableLiveData<ArrayList<Event>>

    fun clickLikeOrDislikeEvent(eventId: String): MutableLiveData<Boolean>

    suspend fun addEventComment(content: String, eventId: String): MutableLiveData<Boolean>

    fun getAllCommentByEventId(eventId: String): MutableLiveData<ArrayList<Comment>>

    fun getEventById(eventId: String): MutableLiveData<Event>

    fun isExistEvent(eventId: String): MutableLiveData<Boolean>

    fun getEventsByUid(currentUid: String): MutableLiveData<ArrayList<Event>>

    fun getCountEventsByUid(currentUid: String): MutableLiveData<Long>

    fun searchEvent(eventName: String): MutableLiveData<ArrayList<Event>>

    fun getEventByDate(date: String): MutableLiveData<ArrayList<Event>>

    suspend fun joinEvent(eventId:String): MutableLiveData<Boolean>

    fun getJoinedEventByDate(date:String):MutableLiveData<ArrayList<Event>>
}