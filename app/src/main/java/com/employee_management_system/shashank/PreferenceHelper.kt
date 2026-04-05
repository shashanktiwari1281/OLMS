package com.employee_management_system.shashank

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceHelper {

    const val PREF_NAME = "userDetails"

    fun getPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)

    fun getUserId(context: Context) = getPreference(context).getString("empId", null)

    fun setUserId(context: Context, userId: String){
        getPreference(context).edit { putString("empId", userId) }
    }

}