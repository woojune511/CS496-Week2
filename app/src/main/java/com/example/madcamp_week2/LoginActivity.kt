package com.example.madcamp_week2

//import android.R

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cs496_week2.LoginCallback
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private var btn_facebook_login: LoginButton? = null
    private var mLoginCallback: LoginCallback? = null
    private var mCallbackManager: CallbackManager? = null

    var txt_create_acccount: TextView? = null
    var edt_login_email: EditText? = null
    var edt_login_password: EditText? = null
    var btn_login: Button? = null
    var compositeDisposable = CompositeDisposable()
    var iMyService: iMyService? = null
    public override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var intent: Intent = Intent(applicationContext, MainActivity::class.java)

        btn_facebook_login = findViewById<View>(R.id.btn_facebook_login) as LoginButton
        btn_facebook_login!!.setReadPermissions(
            listOf(
                "public_profile", "email", "user_birthday", "user_friends"
            )
        )

//        getHashKey(mContext);
        mCallbackManager = CallbackManager.Factory.create()
        btn_facebook_login!!.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                val request = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response ->
                    Log.v("LoginActivity", response.toString())

                    // Application code
                    val email = `object`.getString("email")
                    val birthday =
                        `object`.getString("birthday") // 01/31/1980 format

                    val user_info: JSONObject = response.jsonObject.getJSONObject("graphObject")
                    val user_id: String = user_info.getString("id")
                    val user_name: String = user_info.getString("name")
                    val user_email: String = user_info.getString("email")

                    intent.putExtra("user_id", user_id)
                    intent.putExtra("user_name", user_name)
                    intent.putExtra("user_email", user_email)
                }


                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                request.parameters = parameters
                request.executeAsync()
                startActivity(intent)
                finish()
            }

            override fun onCancel() {
                // App code
                Log.v("LoginActivity", "cancel")
            }

            override fun onError(exception: FacebookException) {
                // App code
                Log.v("LoginActivity", exception.cause.toString())
            }

            // 앱 켰을 때 로그인 되어 있는 상태면 바로 mainactivity로 넘어가게 하기
        })


/*
        mLoginCallback = object : LoginCallback() {
            override fun onSuccess(loginResult: LoginResult?) {
                val accessToken = loginResult!!.accessToken
                val request =
                    GraphRequest.newMeRequest(
                        accessToken
                    ) { user, response -> //LoginManager.getInstance().logOut();
                        val username = user.optString("name")
                    }.executeAsync()
                Toast.makeText(
                    applicationContext,
                    "Login Succeeded with FaceBook",
                    Toast.LENGTH_SHORT
                ).show()
                //MainActivity로 넘어가기 전에 유저 정보를 넘겨줘야 하나?
                startActivity(Intent(mContext, MainActivity::class.java))
                finish()
            }

            override fun onCancel() {}
            override fun onError(e: FacebookException) {}
        }
        */

//        mLoginCallback = object : LoginCallback() {
//            fun onSuccess (loginResult: LoginResult) : LoginCallback {
//                val accessToken: AccessToken = loginResult.accessToken
//                val request: GraphRequestAsyncTask =
//                    GraphRequest.newMeRequest(accessToken
//                    ) { user, response -> //LoginManager.getInstance().logOut();
//                        val username = user.optString("name")
//                    }.executeAsync()
//                Toast.makeText(
//                    applicationContext,
//                    "Login Succeeded with FaceBook",
//                    Toast.LENGTH_SHORT
//                ).show()
//                //MainActivity로 넘어가기 전에 유저 정보를 넘겨줘야 하나?
//                startActivity(Intent(mContext, MainActivity::class.java))
//                finish()
//            }
//
//            fun onCancel() {}
//            fun onError(e: FacebookException?) {}
//        }

//        btn_facebook_login!!.registerCallback(mCallbackManager, mLoginCallback)
    }

    private fun loginUser(email: String, password: String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be null or empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be null or empty", Toast.LENGTH_SHORT).show()
            return
        }
        compositeDisposable.add(iMyServiceBak!!.loginUser(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response -> Toast.makeText(this@MainActivity, "" + response, Toast.LENGTH_SHORT).show() })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun getHashKey(context: Context): String? {
            val TAG = "KeyHash"
            var keyHash: String? = null
            try {
                val info = context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                for (signature in info.signatures) {
                    var md: MessageDigest
                    md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    keyHash = String(Base64.encode(md.digest(), 0))
                    Log.d(TAG, keyHash)
                }
            } catch (e: Exception) {
                Log.e("name not found", e.toString())
            }
            return keyHash
        }
    }
}