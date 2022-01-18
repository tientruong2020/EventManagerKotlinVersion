package com.example.myapplication.data.local

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.myapplication.model.HistoricalSearchingText

class LocalDBRepository(application: Application) {
    val localDBDAO:LocalDBDAO? = LocalDatabase.getDatabase(application)?.localDBDAO()

    val getAllHistory:LiveData<List<HistoricalSearchingText>>? = localDBDAO?.getAllSearchingHistory()

    suspend fun insertSearchingHistory(historicalSearchingText: HistoricalSearchingText){
        localDBDAO?.insertSearchingText(historicalSearchingText)
    }

    suspend fun deleteHistory(historicalSearchingText: HistoricalSearchingText){
        localDBDAO?.deleteSearchingHistory(historicalSearchingText)
    }

}