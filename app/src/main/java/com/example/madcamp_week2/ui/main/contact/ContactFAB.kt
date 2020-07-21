package com.example.madcamp_week2.ui.main.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.IMyService
import com.example.madcamp_week2.R
import com.example.madcamp_week2.UserInfo
import com.example.madcamp_week2.app
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ContactFAB(): AppCompatActivity() {

    val API_URL: String = "http://192.249.19.242:6180/"

    var userinfo: UserInfo? = null

    val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).build()

    val iMyService: IMyService = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client).build().create<IMyService>(IMyService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.fragment_contact_add)
        val nameInput = findViewById<View>(R.id.NameInput) as EditText
        val numInput = findViewById<View>(R.id.NumInput) as EditText
        val confirmButton = findViewById<View>(R.id.ConfirmButton) as Button
        val cancelButton = findViewById<View>(R.id.CancelButton) as Button
        confirmButton.setOnClickListener {
            val intent = Intent()
            val strName = nameInput.text.toString()
            val strNumber = numInput.text.toString()

            when {
                TextUtils.isEmpty(strName) -> {
                    Toast.makeText(this, "Name cannot be null or empty", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(strNumber) -> {
                    Toast.makeText(this, "Number cannot be null or empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    intent.putExtra("newName", strName)
                    intent.putExtra("newNum", strNumber)
                    setResult(Activity.RESULT_OK, intent)
                    //parent.writeContact(strName, strNumber)

                    var req: Call<String> = iMyService!!.addContact(strName, strNumber, app.prefs.id.toString())
                    req.enqueue(object: Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>){
                            //Log.d("userinfo", response.body().toString())
                            var result: String? = response.body()
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.d("userinfo", "fuck")
                        }
                    })



                    finish()
                }
            }
        }
        cancelButton.setOnClickListener { finish() }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return event.action != MotionEvent.ACTION_OUTSIDE
    }

    override fun onBackPressed() {
        return
    }
}