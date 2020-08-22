package com.inyongtisto.todoapp.activity.splash

import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.User

interface SplashListener {
    fun onSuccess(data: ResponModel)
    fun onFailure(message: String)
}