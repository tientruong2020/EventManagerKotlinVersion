package com.example.myapplication.ui.main.changepassworddialog

import android.text.TextUtils

object ChangePasswordValidator {

    fun isFieldsEmpty(field:String):Boolean{
        return field.length >=6
    }

    fun checkRepeatPassword(password: String, repeatPassword: String): Boolean{
        return TextUtils.equals(password, repeatPassword)
    }
}