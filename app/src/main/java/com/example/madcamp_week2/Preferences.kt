package com.example.madcamp_week2

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.System.putString
import com.facebook.AccessToken


class Preferences(context: Context) {

    val PREFS_FILENAME = "prefs"
    val PREF_KEY_MY_EDITTEXT = "myEditText"
    val EMAIL = "email"
    val ID = "id"
    val ISCHAT = "isChat"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */

    var myEditText: String?
        get() = prefs.getString(PREF_KEY_MY_EDITTEXT, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var email: String?
        get() = prefs.getString(EMAIL, "")
        set(value) = prefs.edit().putString(EMAIL, value).apply()

    var id: String?
        get() = prefs.getString(ID, "")
        set(value) = prefs.edit().putString(ID, value).apply()

    var isChat: Boolean?
        get() = prefs.getBoolean(ISCHAT, false)
        set(value) = prefs.edit().putBoolean(ISCHAT, value!!).apply()
    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
     * set(value) 실행 시 value로 값을 대체한 후 저장 */
}