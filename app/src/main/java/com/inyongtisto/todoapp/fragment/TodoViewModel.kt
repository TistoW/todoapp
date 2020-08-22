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

            override fun onChanged(message: String) {

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

            override fun onChanged(message: String) {

            }

        })
    }

    fun updateTodo(todo: Todo) {
        Log.d("ini bukan update", "check")
        repo.updateTodo(todo, object : TodoListener{
            override fun onSuccess(data: ArrayList<Todo>) {
            }

            override fun onFailure(message: String) {
            }

            override fun onChanged(message: String) {
                listener!!.onChanged(message)
                Log.d("ini bukan dua", "check")
            }

        })
    }

    fun deleteTodo(todo: Todo) {
        repo.deleteTodo(todo)
    }
}