package com.example.madcamp_week2.ui.main.contact

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.madcamp_week2.IMyService
import com.example.madcamp_week2.R
import com.example.madcamp_week2.UserInfo
import com.example.madcamp_week2.app
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

class ContactAdd(val parent: ContactFragment) : DialogFragment() {

    val API_URL: String = "http://192.249.19.242:6180/"

    var userinfo: UserInfo? = null

    val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).build()

    val iMyService: IMyService = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client).build().create<IMyService>(IMyService::class.java)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.add_contact, null)
        builder.setView(view)
        val submit = view.findViewById<View>(R.id.ConfirmButton) as Button
        val name = view.findViewById<View>(R.id.edittextEmailAddress) as EditText
        val number = view.findViewById<View>(R.id.edittextPassword) as EditText
        submit.setOnClickListener {
            Log.d("add", "add invoked")
            val strName = name.text.toString()
            val strNumber = number.text.toString()
            val data = Intent()
            data.putExtra("name", strName)
            data.putExtra("number", strNumber)
            targetFragment!!.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                data
            )
//            @POST("contact/add/{uid}")
//            @FormUrlEncoded
//            fun addContact(@Field("name") name: String?,
//                           @Field("number") number: String?,
//                           @Path("uid") uid: Int?): Observable<String?>?
            //shared~~
            var compositeDisposable = CompositeDisposable()
//            compositeDisposable.add(iMyService!!.loginUser(email, password)!!
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { response -> Toast.makeText(this@LoginActivity, "" + response, Toast.LENGTH_SHORT).show() })

//            compositeDisposable.add(iMyService!!.addContact(strName, strNumber, app.prefs.id.toString())!!
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { response -> Toast.makeText(parent.requireActivity(), "" + response, Toast.LENGTH_SHORT).show() })

            dismiss()
        }
        return builder.create()
    }
}