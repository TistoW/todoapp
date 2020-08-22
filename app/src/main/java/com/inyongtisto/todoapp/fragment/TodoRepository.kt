package com.inyongtisto.todoapp.fragment

import androidx.lifecycle.MutableLiveData
import com.inyongtisto.todoapp.app.ApiConfig
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.Todo
import com.inyongtisto.todoapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoRepository {
    val showProgress = MutableLiveData<Boolean>()
    val listTask = MutableLiveData<ArrayList<Todo>>()

    fun changeProgress() {
        showProgress.value = !(showProgress.value != null && showProgress.value!!)
    }

    fun getTodo(id: String, listener: TodoListener) {
        ApiConfig.instanceRetrofit.getTodo(id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showProgress.value = false
                listener.onFailure(t.message.toString())
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                showProgress.value = false
                val res = response.body()!!
                if (res.success == 1) {
//                    listTask.value = res.todos
                    listener.onSuccess(res.todos)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }

    fun createTodo(todo: Todo, listener: TodoListener) {
        ApiConfig.instanceRetrofit.createTodo(todo).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showProgress.value = false
                listener.onFailure(t.message.toString())
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                showProgress.value = false
                val res = response.body()!!
                if (res.success == 1) {
                    listener.onSuccess(res.todos)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }

    fun updateTodo(todo: Todo) {
        ApiConfig.instanceRetrofit.updateTodo(todo).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

            }
        })
    }

    fun deleteTodo(todo: Todo) {
        ApiConfig.instanceRetrofit.deleteTodo(todo).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

            }
        })
    }
}