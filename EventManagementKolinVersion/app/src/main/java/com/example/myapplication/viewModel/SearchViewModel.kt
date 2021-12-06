package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Data.Remote.RTDBRepositoryImpl
import com.example.myapplication.Data.Remote.UserRepositoryImpl
import com.example.myapplication.Data.local.LocalDBRepository
import com.example.myapplication.model.Event
import com.example.myapplication.model.HistoricalSearchingText
import com.example.myapplication.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class SearchViewModel(application: Application): AndroidViewModel(application) {
    private val localDBRepo : LocalDBRepository
    private val userRepositoryImpl: UserRepositoryImpl
    private val rtdbRepositoryImpl: RTDBRepositoryImpl
    var allSearchingHistory:LiveData<List<HistoricalSearchingText>>

    init {
        localDBRepo = LocalDBRepository(application)
        userRepositoryImpl = UserRepositoryImpl(application)
        allSearchingHistory = localDBRepo.getAllHistory!!
        rtdbRepositoryImpl = RTDBRepositoryImpl()
    }


    fun addSearchingHistory(historicalSearchingText: HistoricalSearchingText){
        viewModelScope.launch(Dispatchers.IO) {
            localDBRepo.insertSearchingHistory(historicalSearchingText)
        }
    }

    fun deleteHistory(historicalSearchingText: HistoricalSearchingText){
        viewModelScope.launch(Dispatchers.IO) {
            localDBRepo.deleteHistory(historicalSearchingText)
        }
    }

    fun searchUser(username: String):MutableLiveData<ArrayList<User>>{
        return userRepositoryImpl.searchUser(username)
    }

    fun searchEvent(eventName: String):MutableLiveData<ArrayList<Event>>{
        return rtdbRepositoryImpl.searchEvent(eventName)
    }
}