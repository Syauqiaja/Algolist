package com.aglotest.algolist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    val isLoading : LiveData<Boolean> get() = _isLoading
    val _isLoading = MutableLiveData(false)

    init {
        viewModelScope.launch {
            _isLoading.value = false
            delay(2000)
            _isLoading.value = true
        }
    }
}