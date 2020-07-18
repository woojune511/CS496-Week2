package com.example.madcamp_week1.ui.main.contact

import com.example.madcamp_week1.R
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// setting node.js server convention -> we should transmit information using header
interface sendContactService {
    @Headers(
        "name: ${R.id.name}",
        "number: ${R.id.number}")
    // receive that register in DB is okay
    @GET("/login")
    fun getContactRes(@Query("contact") contact: PhoneBook): Call<PhoneBook>
}