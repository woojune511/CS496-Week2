package com.example.madcamp_week1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        Log.d("Pref", ">> onCreatePreferences()")

        val themeSettings = findPreference<ListPreference>("theme_color") !!
        themeSettings.summary = themeSettings.value
        themeSettings.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ preference, newValue ->
            preference.summary = newValue as String
            true
        }
    }
}