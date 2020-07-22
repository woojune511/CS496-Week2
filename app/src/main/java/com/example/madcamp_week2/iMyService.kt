package com.example.madcamp_week2

import com.example.madcamp_week2.ui.main.contact.PhoneBook
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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

    @POST("contact/add/{fb_id}")
    @FormUrlEncoded
    fun addContact(@Field("name") name: String?,
                  @Field("number") number: String?,
                    @Path("fb_id") fb_id: String?): Call<String>

    @POST("contact/{fb_id}")
    @FormUrlEncoded
    fun findUserbyFB(@Path("fb_id") fb_id: String?,
                   @Field("email") email: String?): Call<UserInfo>

    @POST("contact/delete/{fb_id}")
    @FormUrlEncoded
    fun deleteContact(@Field("number") number: String?,
                        @Path("fb_id") fb_id: String?)

    @POST("contact/find/{fb_id}")
    @FormUrlEncoded
    fun findUserbyName(@Field("name") name: String?,
                        @Path("fb_id") fb_id: String?)

    @GET("contact/find/{fb_id}")
    fun getAllContacts(@Path("fb_id") fb_id: String?): Call<List<PhoneBook>>

    @Multipart
    @POST("gallery/upload")
    fun postImage(@Part image: MultipartBody.Part?, @Part("upload") name: RequestBody?, @Part("path") pathName: RequestBody?): Call<ResponseBody?>?

    @GET("gallery/allimages")
    fun getImageList(): Call<List<GalleryInfo>>
}