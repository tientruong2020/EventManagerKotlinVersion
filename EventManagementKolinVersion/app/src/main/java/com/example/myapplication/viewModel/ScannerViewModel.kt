package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Data.Remote.RTDBRepositoryImpl

class ScannerViewModel(application: Application): AndroidViewModel(application) {

    private val rtdbRepositoryImpl:RTDBRepositoryImpl
     init {
         rtdbRepositoryImpl = RTDBRepositoryImpl()
     }

    fun isExitEvent(eventId:String):MutableLiveData<Boolean>{
        return rtdbRepositoryImpl.isExistEvent(eventId)
    }
}