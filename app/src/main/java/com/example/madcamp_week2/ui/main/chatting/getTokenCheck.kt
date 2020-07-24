package com.example.madcamp_week2.ui.main.chatting

import android.content.ContentValues.TAG
import android.content.Intent
import com.facebook.login.LoginManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madcamp_week2.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.example.madcamp_week2.R
import com.example.madcamp_week2.app
import com.example.madcamp_week2.ui.main.chatting.ChatActivity
import com.example.madcamp_week2.ui.main.gallery.GalleryFragment
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext

class getTokenCheck : Fragment() {
    private lateinit var auth: FirebaseAuth

    //private lateinit var binding: ActivityFacebookBinding
    private lateinit var callbackManager: CallbackManager

    val token: AccessToken? = AccessToken.getCurrentAccessToken()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        //FacebookSdk.sdkInitialize(getApplicationContext())
        //auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentUser = auth.currentUser
//        Log.d("token", currentUser!!.uid)
        if(app.prefs.isChat == false) {
            app.prefs.isChat = true
            startActivity(Intent(context, ChatActivity::class.java))
        } else {
            app.prefs.isChat = false
        }
        Log.d("token", "done")

        return super.onCreateView(inflater, container, savedInstanceState)
    }



//    override fun onStart() {
//        super.onStart()
//    }

//    override fun onResume() {
//        super.onResume()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        Log.d("token", currentUser!!.uid)
//        if(app.prefs.isChat == false) {
//            app.prefs.isChat = true
//            startActivity(Intent(context, ChatActivity::class.java))
//        } else {
//            app.prefs.isChat = false
//        }
//        Log.d("token", "done")
//        //startActivity(Intent(context, GalleryFragment::class.java))
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        Log.d("token", currentUser!!.uid)
//        if(app.prefs.isChat == false) {
//            app.prefs.isChat = true
//            startActivity(Intent(context, ChatActivity::class.java))
//        } else {
//            app.prefs.isChat = false
//        }
//        Log.d("token", "done")
//        //startActivity(Intent(context, GalleryFragment::class.java))
//    }



//    var intent: Intent = Intent(applicationContext, ChatActivit ::class.java)
//    startActivity(intent)
}