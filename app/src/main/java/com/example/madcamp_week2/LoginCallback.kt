package com.example.cs496_week2

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginResult


open class LoginCallback : FacebookCallback<LoginResult?> {
    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.
    override fun onSuccess(loginResult: LoginResult?) {
        Log.d("Success", loginResult?.accessToken.toString())
        Log.d("Success", Profile.getCurrentProfile().id.toString())
        Log.d("Success", Profile.getCurrentProfile().name)
        Log.d("Success", Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString())
//        val accessToken = loginResult!!.accessToken

//        val request = GraphRequest.newMeRequest(accessToken) { user, _ ->
//                Log.d(TAG, user.optString("email"))
//                Log.d(TAG, user.optString("name"))
//                Log.d(TAG, user.optString("id"))
//            }.executeAsync()
        Log.e("Callback :: ", "onSuccess")
        requestMe(loginResult?.accessToken)
//        println("\n\n"+Profile.getCurrentProfile().id+"\n\n")
    }

    // 로그인 창을 닫을 경우, 호출됩니다.
    override fun onCancel() {
        Log.e("Callback :: ", "onCancel")
    }

    // 로그인 실패 시에 호출됩니다.
    override fun onError(error: FacebookException) {
        Log.e("Callback :: ", "onError : " + error.message)
    }

//    override fun onResume(){
//        super.onResume();
//        var profile: Profile = Profile.getCurrentProfile()
//        neexA
//    }

    // 사용자 정보 요청
    fun requestMe(token: AccessToken?) {
        val graphRequest: GraphRequest = GraphRequest.newMeRequest(token
        ) { `object`, _ -> Log.e("result", `object`.toString())}
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,gender,birthday")

        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

}