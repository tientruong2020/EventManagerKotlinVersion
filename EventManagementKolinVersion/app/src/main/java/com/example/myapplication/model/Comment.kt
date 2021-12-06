package com.example.myapplication.model

class Comment {
    var id: String? = null
    var uid:String = ""
    var content:String = ""
    var createdAt:Long = 0
    var user: User? = null

    constructor(){}

    constructor(uid: String, content: String, createdAt: Long) {
        this.uid = uid
        this.content = content
        this.createdAt = createdAt
    }

    constructor(id: String, user: User, content: String, createdAt: Long) {
        this.id = id
        this.uid = uid
        this.content = content
        this.createdAt = createdAt
        this.user = user
    }

}