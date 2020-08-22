package com.inyongtisto.todoapp.activity.baru

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class BaruViewModel : ViewModel() {

    private val repo = BaruRepository()
    val showProgress : LiveData<Boolean>

    init {
        this.showProgress = repo.showProgress
    }

    fun onProgress(){
        repo.changeProgress()
    }

}