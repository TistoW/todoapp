package com.inyongtisto.todoapp.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.inyongtisto.todoapp.model.Todo

class TodoViewModel : ViewModel() {

    private val repo = TodoRepository()
    val showProgress: LiveData<Boolean>
    val listTodo: LiveData<ArrayList<Todo>>
    var listener: TodoListener? = null

    init {
        this.showProgress = repo.showProgress
        this.listTodo = repo.listTask
    }

    fun onProgress() {
        repo.changeProgress()
    }

    fun getTodo(id: String) {
        repo.getTodo(id, object : TodoListener{
            override fun onSuccess(data: ArrayList<Todo>) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }

    fun createTodo(todo: Todo) {
        repo.createTodo(todo, object : TodoListener{
            override fun onSuccess(data: ArrayList<Todo>) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }

    fun updateTodo(todo: Todo) {
        repo.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo) {
        repo.deleteTodo(todo)
    }
}