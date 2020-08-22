package com.inyongtisto.todoapp.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.Todo
import com.inyongtisto.todoapp.model.User
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    private val repo = MainRepository()
    val showProgress: LiveData<Boolean>
    val listTask: LiveData<ArrayList<Task>>
    val task: LiveData<Task>
    var listener: MainListener? = null

    init {
        this.showProgress = repo.showProgress
        this.listTask = repo.listTask
        this.task = repo.task
    }

    fun onProgress() {
        repo.changeProgress()
    }

    fun getTask(user: User) {
        repo.getTask(user)
    }

    fun createTask(data: Task) {
        repo.createTask(data, object : MainListener {
            override fun onSuccess(data: Task) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }

    fun updateTask(task: Task) {
        repo.updateTask(task)
    }

    fun deleteTask(task: Task) {
        repo.deleteTask(task)
    }

}