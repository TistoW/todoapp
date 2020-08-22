package com.inyongtisto.todoapp.activity.splash

import androidx.lifecycle.ViewModel
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.User

class SplashViewModel : ViewModel() {

    private val repo = SplashRepository()
    var listener:SplashListener? = null

    fun getTask(user: User){
        repo.getTask(user, object :SplashListener{
            override fun onSuccess(data: ResponModel) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }

}