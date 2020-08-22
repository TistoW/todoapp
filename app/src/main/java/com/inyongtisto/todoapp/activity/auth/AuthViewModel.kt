package com.inyongtisto.todoapp.activity.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.User

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()
    val showProgress: LiveData<Boolean>
//    val user: LiveData<User>
    var listener: AutListener? = null

    init {
        this.showProgress = repo.showProgress
    }

    fun onProgress() {
        repo.changeProgress()
    }

    fun login(user: User) {
        repo.login(user, object :AutListener{
            override fun onSuccess(data: ResponModel) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }

    fun loginGoogle(user: User) {
        repo.loginGoogle(user, object :AutListener{
            override fun onSuccess(data: ResponModel) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }

    fun register(user: User) {
        repo.register(user, object :AutListener{
            override fun onSuccess(data: ResponModel) {
                listener!!.onSuccess(data)
            }

            override fun onFailure(message: String) {
                listener!!.onFailure(message)
            }
        })
    }
}