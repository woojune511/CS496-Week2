package com.example.madcamp_week2

//import android.R

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    val API_URL: String = "http://192.249.19.242:6180/"

    var userinfo: UserInfo? = null

    val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).build()

    val iMyService: IMyService = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client).build().create<IMyService>(IMyService::class.java)

    private var mContext: Context? = this
    private var btn_facebook_login: LoginButton? = null
    private var mLoginCallback: LoginCallback? = null
    private var mCallbackManager: CallbackManager? = null

    var txt_create_acccount: TextView? = null
    var edt_login_email: EditText? = null
    var edt_login_password: EditText? = null
    var btn_login: Button? = null
    var compositeDisposable = CompositeDisposable()
    public override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    fun isLogin(): Boolean {
        val token: AccessToken? = AccessToken.getCurrentAccessToken()
        if(token != null){
//            Log.d("login", token.toString())
            startActivity(intent)
            finish()
            return true
        } else return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d("login", "start")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_facebook_login = findViewById<View>(R.id.btn_facebook_login) as LoginButton
        btn_facebook_login!!.setReadPermissions(
            listOf(
                "public_profile", "email", "user_birthday", "user_friends"
            )
        )

//        getHashKey(mContext);
        mCallbackManager = CallbackManager.Factory.create()
//        Log.d("login", mCallbackManager.toString())
        btn_facebook_login!!.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
//                Log.d("login", "success")
                var auth: FirebaseAuth = FirebaseAuth.getInstance()
                var intent: Intent = Intent(applicationContext, MainActivity::class.java)

                // App code
                val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                    //Log.d("login json", response.jsonObject.toString())

                    // Application code
                    val email = response.jsonObject.getString("email")
                    val id = response.jsonObject.getString("id")
//                    val birthday = `object`.getString("birthday") // 01/31/1980 format
                    //intent.putExtra("token", loginResult.accessToken)
//                    intent.putExtra("user_email", email)
//                    intent.putExtra("user_id", id)

//                    Log.d("login", email)
//                    Log.d("login", id)
//                    Log.d("login", intent.getStringExtra("user_email").toString())
//                    Log.d("login", intent.getStringExtra("user_id").toString())

                    app.prefs.email = email
                    app.prefs.id = id
//                    Log.d("pref", email)
//                    Log.d("pref", app.prefs.email.toString())
//                    Log.d("pref", id)
//                    Log.d("pref", app.prefs.id.toString())
                }

                val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(Activity()) { task ->
                        val user = auth.currentUser
                    }

                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                request.parameters = parameters
                request.executeAsync()

//                MainActivity()
                startActivity(intent)
                finish()
            }

            override fun onCancel() {
                // App code
                Log.d("login", "cancel")
            }

            override fun onError(exception: FacebookException) {
                // App code
                Log.d("login", exception.cause.toString())
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
        compositeDisposable.add(iMyService!!.loginUser(email, password)!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response -> Toast.makeText(this@LoginActivity, "" + response, Toast.LENGTH_SHORT).show() })
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