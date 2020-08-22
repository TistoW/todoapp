package com.inyongtisto.todoapp.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.inyongtisto.todoapp.model.ResponModel
import com.inyongtisto.todoapp.model.Task
import com.inyongtisto.todoapp.model.User

class SharePref {

    private val statusLogin = "login"
    private val user = "user"
    private val listTask = "listTask"
    private val task = "task"

    private val mypref = "MAIN_PREF"
    private val sp: SharedPreferences?

    constructor(context: Activity) {
        sp = context.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    constructor(context: Context) {
        sp = context.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun setUser(data: User) {
        val json = Gson().toJson(data, User::class.java)
        sp!!.edit().putString(user, json).apply()
    }

    fun getUser(): User? {
        val data = sp!!.getString(user, null) ?: return null
        return Gson().fromJson(data, User::class.java)
    }

    fun setListTask(data: ResponModel) {
        val json = Gson().toJson(data, ResponModel::class.java)
        sp!!.edit().putString(listTask, json).apply()
    }

    fun getListTask(): ResponModel? {
        val data = sp!!.getString(listTask, null) ?: return null
        return Gson().fromJson(data, ResponModel::class.java)
    }

    fun setTaskAktive(data: Task) {
        val json = Gson().toJson(data, Task::class.java)
        sp!!.edit().putString(task, json).apply()
    }

    fun getTaskAktive(): Task? {
        val data = sp!!.getString(task, null) ?: return null
        return Gson().fromJson(data, Task::class.java)
    }

    fun setStatusLogin(status: Boolean){
        sp!!.edit().putBoolean(statusLogin, status).apply()
    }

    fun setString(keySP: String, value: String) {
        sp!!.edit().putString(keySP, value).apply()
    }

    fun getString(keySP: String): String {
        return sp!!.getString(keySP, "")!!
    }

    fun getStatusLogin():Boolean{
        return sp!!.getBoolean(statusLogin, false)
    }

    fun clearAll() {
        sp!!.edit().clear().apply()
    }
}
