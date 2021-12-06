package com.example.myapplication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel ():ViewModel() {
    private val searchingText:MutableLiveData<String> = MutableLiveData()

    fun setSearchingText(input:String){
        searchingText.postValue(input)
    }

    fun getSearchingText():LiveData<String>{
        return searchingText
    }
}