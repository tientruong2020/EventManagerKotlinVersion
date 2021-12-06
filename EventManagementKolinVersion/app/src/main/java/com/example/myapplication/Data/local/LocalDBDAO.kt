package com.example.myapplication.Data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.model.HistoricalSearchingText

@Dao
interface LocalDBDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchingText(historicalSearchingText: HistoricalSearchingText)

    @Query("SELECT * FROM historical_search_table ORDER BY createdAt DESC LIMIT 10")
    fun getAllSearchingHistory():LiveData<List<HistoricalSearchingText>>

    @Delete
    suspend fun deleteSearchingHistory(historicalSearchingText: HistoricalSearchingText)

    @Query("DELETE FROM historical_search_table")
    suspend fun deleteAllSearchHistory()
}