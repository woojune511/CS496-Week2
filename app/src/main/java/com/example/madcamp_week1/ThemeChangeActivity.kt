package com.example.madcamp_week1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager

open class ThemeChangeActivity(private val noActionBar : Boolean = false) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val curTheme = sharedPreferences.getString("theme_color", "theme1")!!
        Log.d("Tag", curTheme!!)
        when(curTheme){
            "theme1" -> {
                if(noActionBar) setTheme(R.style.AppTheme_NoActionBar)
                else setTheme(R.style.AppTheme)
                //window.statusBarColor = getColor(R.color.colorPrimaryDark)
            }
            "theme2" -> {
                if(noActionBar) setTheme(R.style.AppTheme2_NoActionBar)
                else setTheme(R.style.AppTheme2)
            }
        }
        super.onCreate(savedInstanceState)
    }
}