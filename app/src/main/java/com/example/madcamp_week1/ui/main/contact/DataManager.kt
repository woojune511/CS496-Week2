package com.example.madcamp_week1.ui.main.contact

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DataManager() {
    private var contactService: sendContactService? = null
    /*
    private val client: OkHttpClient

    init {
        client = OkHttpClient.Builder()
            .addInterceptor(commonNetworkInterceptor)
            .build()

        createContactAPI()
    }
    */
    private fun createContactAPI() {
        contactService = Retrofit.Builder()
            .baseUrl("http://192.249.19.242:6980")
            //.client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(sendContactService::class.java)
    }
}