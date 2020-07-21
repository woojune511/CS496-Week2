package com.example.madcamp_week2

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface iMyService {
    @POST("register")
    @FormUrlEncoded
    fun registerUser(@Field("email") email: String?,
                     @Field("name") name: String?,
                     @Field("password") password: String?): Observable<String?>?

    @POST("login")
    @FormUrlEncoded
    fun loginUser(@Field("email") email: String?,
                  @Field("password") password: String?): Observable<String?>?

    @POST("contact/add/{uid}")
    @FormUrlEncoded
    fun addContact(@Field("name") name: String?,
                  @Field("number") number: String?,
                    @Path("uid") uid: Int?): Observable<String?>?

    @POST("contact/{fb_id}}")
    @FormUrlEncoded
    fun findUserbyFB(@Field("fb_id") user_id: String?,
                   @Path("email") email: String?): Observable<String?>?
}