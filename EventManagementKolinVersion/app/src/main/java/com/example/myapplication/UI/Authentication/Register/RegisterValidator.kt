package com.example.myapplication.ui.authentication.Register

import android.text.TextUtils
import android.util.Patterns

object RegisterValidator {

    fun isEmail(email: String):Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun emailIsEmpty(email: String):Boolean{
        return TextUtils.isEmpty(email)
    }

    fun checkLeghtPassword(password: String): Boolean{
        return password.length >= 6
    }

    fun checkRepeatPassword(password: String, repeatPassword: String): Boolean{
        return TextUtils.equals(password, repeatPassword)
    }
}