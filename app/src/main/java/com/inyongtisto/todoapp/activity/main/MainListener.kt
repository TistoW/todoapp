package com.inyongtisto.todoapp.activity.main

import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.Todo
import com.inyongtisto.todoapp.model.User

interface MainListener {
    fun onSuccess(data: Task)
    fun onFailure(message: String)
}