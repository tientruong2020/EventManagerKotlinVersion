package com.example.myapplication.ui.main.home

import android.text.TextUtils

object CommentValidator {
    fun isEmptyContent(content:String):Boolean{
        return TextUtils.isEmpty(content)
    }
}