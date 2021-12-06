package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "historical_search_table", indices = [Index(value = ["content"], unique = true)])
data class HistoricalSearchingText(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val content:String,
    val createdAt: Long
) {

}