package com.inyongtisto.todoapp.fragment

import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.Todo
import com.inyongtisto.todoapp.model.User

interface TodoListener {
    fun onSuccess(data: ArrayList<Todo>)
    fun onFailure(message: String)
    fun onChanged(message: String)
}