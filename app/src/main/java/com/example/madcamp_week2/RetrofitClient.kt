package com.example.madcamp_week2

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    var instance: Retrofit? = null
        get() {
            if (field == null) field = Retrofit.Builder()
                .baseUrl("http://192.249.19.242:6180/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return field
        }
        private set
}