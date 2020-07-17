package com.example.madcamp_week1

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : ThemeChangeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        val b = sharedPreferences.getBoolean("theme_color", false)
//        if(b) setTheme(R.style.AppTheme2_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
        Log.d("TAG", ">> onCreate from settingsActivity")
    }
}