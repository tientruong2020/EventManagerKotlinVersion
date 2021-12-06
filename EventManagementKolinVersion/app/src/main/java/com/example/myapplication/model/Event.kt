package com.example.myapplication.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.ArrayList

@IgnoreExtraProperties
class Event {
    var id:String =""
    var eventName:String = ""
    var description:String = ""
    var placeOrUrl:String = ""
    var uid:String = ""
    var endDate:String = ""
    var startDate:String = ""
    var isOnline:Boolean = false
    var limit:Long = 0
    var isLiked:Boolean = false
    var createdAt:Long = System.currentTimeMillis()
    var imagesList = ArrayList<String>()
    var creator:User? = null

    constructor(){}
    constructor(
        eventName: String,
        description: String,
        placeOrUrl: String,
        uid: String,
        endDate: String,
        startDate: String,
        isOnline: Boolean,
        limit: Long,
        createdAt: Long,
        imagesList: ArrayList<String>
    ) {
        this.eventName = eventName
        this.description = description
        this.placeOrUrl = placeOrUrl
        this.uid = uid
        this.endDate = endDate
        this.startDate = startDate
        this.isOnline = isOnline
        this.limit = limit
        this.createdAt = createdAt
        this.imagesList = imagesList
    }

    constructor(
        id: String,
        uid: String,
        user: User,
        eventName: String,
        description: String,
        placeOrUrl: String,
        endDate: String,
        startDate: String,
        isOnline: Boolean,
        limit: Long,
        createdAt: Long,
        imagesList: ArrayList<String>,
        isLiked:Boolean
    ) {
        this.id = id
        this.uid = uid
        this.creator = user
        this.eventName = eventName
        this.description = description
        this.placeOrUrl = placeOrUrl
        this.endDate = endDate
        this.startDate = startDate
        this.isOnline = isOnline
        this.limit = limit
        this.createdAt = createdAt
        this.imagesList = imagesList
        this.isLiked = isLiked
    }
    constructor(
        id: String,
        uid: String,
        eventName: String,
        description: String,
        placeOrUrl: String,
        endDate: String,
        startDate: String,
        isOnline: Boolean,
        limit: Long,
        createdAt: Long,
        imagesList: ArrayList<String>,
    ) {
        this.id = id
        this.uid = uid
        this.eventName = eventName
        this.description = description
        this.placeOrUrl = placeOrUrl
        this.endDate = endDate
        this.startDate = startDate
        this.isOnline = isOnline
        this.limit = limit
        this.createdAt = createdAt
        this.imagesList = imagesList
    }


}