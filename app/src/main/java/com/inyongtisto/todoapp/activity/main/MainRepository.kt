package com.inyongtisto.todoapp.activity.main

import androidx.lifecycle.MutableLiveData
import com.inyongtisto.todoapp.app.ApiConfig
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository {
    val showProgress = MutableLiveData<Boolean>()
    val listTask = MutableLiveData<ArrayList<Task>>()
    val task = MutableLiveData<Task>()

    fun changeProgress() {
        showProgress.value = !(showProgress.value != null && showProgress.value!!)
    }

    fun getTask(user: User) {
        ApiConfig.instanceRetrofit.getTask(user).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    listTask.value = res.tasks
                }
            }
        })
    }

    fun createTask(data: Task, listener: MainListener) {
        ApiConfig.instanceRetrofit.createTask(data).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                listener.onFailure(t.message.toString())
                showProgress.value = false
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                showProgress.value = false
                if (res.success == 1) {
                    listTask.value = res.tasks
                    listener.onSuccess(res.task)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }

    fun updateTask(data: Task) {
        ApiConfig.instanceRetrofit.updateTask(data).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    task.value = res.task
                }
            }
        })
    }

    fun deleteTask(data: Task) {
        ApiConfig.instanceRetrofit.deleteTask(data).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
            }
        })
    }
}