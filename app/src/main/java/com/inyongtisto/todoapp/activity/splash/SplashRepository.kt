package com.inyongtisto.todoapp.activity.splash

import androidx.lifecycle.MutableLiveData
import com.inyongtisto.todoapp.app.ApiConfig
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashRepository {

    fun getTask(user: User, listener: SplashListener){
        ApiConfig.instanceRetrofit.getTask(user).enqueue(object :Callback<ResponModel>{
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                listener.onFailure(t.message.toString())
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1){
                    listener.onSuccess(res)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }
}