package com.example.myapplication.UI.Main.Home

import android.text.TextUtils

object CommentValidator {
    fun isEmptyContent(content:String):Boolean{
        return TextUtils.isEmpty(content)
    }
}