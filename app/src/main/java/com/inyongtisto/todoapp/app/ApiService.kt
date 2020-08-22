package com.inyongtisto.todoapp.app

import com.inyongtisto.todoapp.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun login(
        @Body user: User
    ): Call<ResponModel>

    @POST("login_google")
    fun loginGoogle(
            @Body user: User
    ): Call<ResponModel>

    @POST("register")
    fun register(
        @Body user: User
    ): Call<ResponModel>

    @GET("home")
    fun cekStatus(): Call<ResponModel>

    @POST("task")
    fun getTask(
        @Body user: User
    ): Call<ResponModel>

    @POST("task/create")
    fun createTask(
        @Body task: Task
    ): Call<ResponModel>

    @POST("task/update")
    fun updateTask(
            @Body task: Task
    ): Call<ResponModel>

    @POST("task/delete")
    fun deleteTask(
            @Body task: Task
    ): Call<ResponModel>

    @GET("todo/{id}")
    fun getTodo(
        @Path("id") type: String
    ): Call<ResponModel>

    @POST("todo/create")
    fun createTodo(
            @Body task: Todo
    ): Call<ResponModel>

    @POST("todo/update")
    fun updateTodo(
            @Body task: Todo
    ): Call<ResponModel>

    @POST("todo/delete")
    fun deleteTodo(
            @Body task: Todo
    ): Call<ResponModel>

}