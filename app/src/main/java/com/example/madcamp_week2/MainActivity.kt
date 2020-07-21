package com.example.madcamp_week2

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.madcamp_week2.ui.main.SectionsPagerAdapter
import com.example.madcamp_week2.ui.main.contact.PhoneBook
import com.google.android.material.tabs.TabLayout
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val API_URL: String = "http://192.249.19.242:6180/"

    var userinfo: UserInfo? = null

    val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).build()

    var iMyService: IMyService = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client).build().create<IMyService>(IMyService::class.java)

    fun findUserbyFB(user_id: String, email: String): Boolean{
        var success : Boolean = true
        var req: Call<UserInfo> = iMyService.findUserbyFB(user_id, email)
//        Log.d("findUserbyFB", user_id)
        req.enqueue(object: Callback<UserInfo>{
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>){
                //Log.d("userinfo", response.body().toString())
                userinfo = response.body()
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
//                Log.d("userinfo", "fuck")
                success = false
            }
        })

        return success

//        var userinfo: Call<UserInfo> = iMyService.findUserbyFB(user_id, email)
//        val sharedpref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
//        var editor: SharedPreferences.Editor = sharedpref.edit()
//        editor.putInt("uid", userinfo.request(). .uid)
//        editor.putString("name", userinfo.name)
//        editor.putString("email", userinfo.email)
//        editor.putString("fb_id", userinfo.fb_result)
//        editor.commit()
    }

    fun getAllContacts(fb_id: String): List<PhoneBook>{
        var contact_list: List<PhoneBook> = iMyService.getAllContacts(fb_id) as List<PhoneBook>
        return contact_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d("login", "main onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Log.d("login", "main onCreateView")
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        getLoginData()


//        val fab: FloatingActionButton = findViewById(R.id.fab)
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }

    fun getLoginData() {

        var intent: Intent = intent
//        var user_name: String = intent.getStringExtra("user_name")!!
//        var user_email: String = intent.getStringExtra("user_email").toString()
//        var user_id: String = intent.getStringExtra("user_id").toString()

        val sharedPref: SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        var user_id: String = app.prefs.id.toString()
//        Log.d("getLoginData", user_id)
        var user_email: String = app.prefs.email.toString()
        //sharedPref.getString("id", user_id)
        //sharedPref.getString("email", user_email)

        //Log.d("login", userinfo?.uid.toString())

        // DB에서 유저 검색하고 uid 가져옴
        // id와 이메일이 둘 다 존재하지 않는다? -> 새로운 페이스북 계정 등록,
        // id는 없는데 이메일이 존재한다? -> 기존 계정에 페이스북 ID만 등록
        // id와 이메일이 둘 다 존재한다? 혹은 id만 있고 이메일이 없다?-> uid만 가져오고 끝

        if(findUserbyFB(user_id, user_email)){
//            Log.d("login", "ssibal main activity running")
//            Log.d("login", user_email)
//            Log.d("login", user_id)
//            Log.d("login", userinfo?.uid.toString())
        }

    }
}