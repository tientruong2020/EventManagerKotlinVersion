package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.remote.RTDBRepositoryImpl
import com.example.myapplication.model.Event
import kotlin.collections.ArrayList

class CalendarViewModel(application: Application): AndroidViewModel(application) {

    private val rtdbRepositoryImpl: RTDBRepositoryImpl = RTDBRepositoryImpl()

    fun getEventsByDate(pickedDate: String):MutableLiveData<ArrayList<Event>>{
        return rtdbRepositoryImpl.getEventByDate(pickedDate)
    }

    fun getJoinedEventsBYDate(pickedDate: String):MutableLiveData<ArrayList<Event>>{
        return rtdbRepositoryImpl.getJoinedEventByDate(pickedDate)
    }
}