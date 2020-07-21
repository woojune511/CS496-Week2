package com.example.madcamp_week2

import com.google.gson.annotations.SerializedName

class UserInfo(
    @field:SerializedName("_id") val id: String,
    val uid: Int,
    val name: String,
    val salt: String,
    val email: String,
    val password: String,
    val fb_result: String,
    @field:SerializedName("_v") val v: Int
)
