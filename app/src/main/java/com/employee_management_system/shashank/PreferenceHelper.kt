package com.employee_management_system.shashank

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceHelper {

    const val PREF_NAME = "MyPrefs"

    fun getPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getUserId(context: Context) = getPreference(context).getString("userId", null)

    fun setUserId(context: Context, userId: String){
        getPreference(context).edit { putString("userId", userId) }
    }

}