package com.inyongtisto.todoapp.activity.auth

import com.inyongtisto.todoapp.model.ResponModel

interface AutListener {
    fun onSuccess(data: ResponModel)
    fun onFailure(message: String)
}