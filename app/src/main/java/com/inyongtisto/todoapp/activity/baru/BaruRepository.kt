package com.inyongtisto.todoapp.activity.baru

import androidx.lifecycle.MutableLiveData

class BaruRepository {
    val showProgress = MutableLiveData<Boolean>()
    fun changeProgress(){
        showProgress.value = !(showProgress.value != null && showProgress.value!!)
    }
}