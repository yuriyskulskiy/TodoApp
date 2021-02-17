package com.skul.testappgl.network

import com.skul.testappgl.data.Task

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .baseUrl("https://jsonplaceholder.typicode.com/")
    .client(OkHttpClient())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface TodoService {
    @GET("todos?userId=1")
    suspend fun getRemoteTasks(): List<Task>
}

object TasksApi {
    val retrofitService: TodoService by lazy { retrofit.create(TodoService::class.java) }
}