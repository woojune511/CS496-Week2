package com.example.madcamp_week2

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*


interface IMyService {
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

    @POST("contact/{fb_id}")
    @FormUrlEncoded
    fun findUserbyFB(@Path("fb_id") fb_id: String?,
                   @Field("email") email: String?): Call<UserInfo>

    @POST("contact/delete/{uid}")
    @FormUrlEncoded
    fun deleteContact(@Field("number") number: String?,
                        @Path("uid") uid: Int?)

    @POST("contact/find/{uid}")
    @FormUrlEncoded
    fun findUserbyName(@Field("name") name: String?,
                        @Path("uid") uid: Int?)

    @GET("contact/find/{uid}")
    @FormUrlEncoded
    fun getAllContacts(@Path("uid") uid: Int?): List<EntryInfo>
}