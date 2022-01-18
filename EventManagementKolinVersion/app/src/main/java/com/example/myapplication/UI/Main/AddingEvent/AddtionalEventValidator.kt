package com.example.myapplication.ui.main.addingevent

import android.text.TextUtils
import android.util.Patterns
import androidx.annotation.NonNull
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.util.*

object AddtionalEventValidator {

    fun validateWebUrl(webUrl: String): Boolean {
        return Patterns.WEB_URL.matcher(webUrl).matches()
    }

    fun isEmptyField(field: String): Boolean {
        return TextUtils.isEmpty(field)
    }

    fun checkEndDate(@NonNull startDateStr: String, @NotNull endDateStr: String): Boolean {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val startDate: Date = simpleDateFormat.parse(startDateStr.trim())
        val endDate: Date = simpleDateFormat.parse(endDateStr.trim())
        if (endDate.before(startDate) || endDate.equals(startDate)) {
            return false
        }
        return true
    }

    fun isNotEmptyAllFields(
        eventName: String,
        placeOrUrl: String,
        description: String,
        startDate: String,
        endDate: String,
        limit: String
    ): Boolean {
        return !(TextUtils.isEmpty(eventName) || TextUtils.isEmpty(placeOrUrl) || TextUtils.isEmpty(
            description
        ) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(limit))
    }
}