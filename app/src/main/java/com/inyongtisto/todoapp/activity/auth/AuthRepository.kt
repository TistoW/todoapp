package com.inyongtisto.todoapp.activity.auth

import androidx.lifecycle.MutableLiveData
import com.inyongtisto.todoapp.app.ApiConfig
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    val showProgress = MutableLiveData<Boolean>()
//    val user = MutableLiveData<User>()
//    val message = MutableLiveData<String>()

    fun changeProgress() {
        showProgress.value = !(showProgress.value != null && showProgress.value!!)
    }

    fun login(u: User, listener: AutListener) {
        ApiConfig.instanceRetrofit.login(u).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showProgress.value = false
                listener.onFailure(t.message!!)
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                showProgress.value = false
                val res = response.body()!!
                if (res.success == 1) {
                    listener.onSuccess(res)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }

    fun lupaPassword(u: User, listener: AutListener) {
        ApiConfig.instanceRetrofit.lupaPassword(u).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showProgress.value = false
                listener.onFailure(t.message!!)
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                showProgress.value = false
                val res = response.body()!!
                if (res.success == 1) {
                    listener.onSuccess(res)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }

    fun loginGoogle(u: User, listener: AutListener) {
        ApiConfig.instanceRetrofit.loginGoogle(u).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showProgress.value = false
                listener.onFailure(t.message!!)
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                showProgress.value = false
                val res = response.body()!!
                if (res.success == 1) {
                    listener.onSuccess(res)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }

    fun register(u: User, listener: AutListener) {
        ApiConfig.instanceRetrofit.register(u).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showProgress.value = false
                listener.onFailure(t.message!!)
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                showProgress.value = false
                val res = response.body()!!
                if (res.success == 1) {
                    listener.onSuccess(res)
                } else {
                    listener.onFailure(res.message)
                }
            }
        })
    }
}