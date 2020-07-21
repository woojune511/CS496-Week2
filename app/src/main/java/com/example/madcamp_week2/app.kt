package com.example.madcamp_week2

import android.app.Application

class app : Application() {
    init {
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: app
        lateinit var prefs : Preferences
    }
    /* prefs라는 이름의 MySharedPreferences 하나만 생성할 수 있도록 설정. */

    override fun onCreate() {
        prefs = Preferences(applicationContext)
        super.onCreate()
    }
}