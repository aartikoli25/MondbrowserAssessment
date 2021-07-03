package com.aarti.mbassignment.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object SharePreferenceUtility {

    val PREFTYPE_BOOLEAN = 0
    val PREFTYPE_INT = 1
    val PREFTYPE_STRING = 2
    val PREFTYPE_LONG = 3
    val PREFTYPE_USER_DETAILS = 3

    @Synchronized
    fun saveBooleanPreferences(context: Context?, strKey: String, booleanValue: Boolean) {
        try {
            if (context != null) {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = sharedPreferences.edit()
                editor.putBoolean(strKey, booleanValue)
                editor.commit()
            }
        } catch (e: Exception) {
        }

    }

    @Synchronized
    fun getPreferences(context: Context, key: String, preferenceDataType: Int): Any? {
        var value: Any? = null
        val sharedPreferences: SharedPreferences
        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            when (preferenceDataType) {
                PREFTYPE_BOOLEAN -> value = sharedPreferences.getBoolean(key, false)
                PREFTYPE_INT -> value = sharedPreferences.getInt(key, 0)
                PREFTYPE_STRING -> value = sharedPreferences.getString(key, "")
                PREFTYPE_LONG -> value = sharedPreferences.getLong(key, 0L)
                PREFTYPE_USER_DETAILS -> {
                }
            }
        } catch (e: Exception) {
            when (preferenceDataType) {
                PREFTYPE_BOOLEAN -> value = false
                PREFTYPE_INT -> value = 0
                PREFTYPE_STRING -> value = ""
                PREFTYPE_LONG -> value = 0L
                PREFTYPE_USER_DETAILS -> value = ""
            }
        }

        return value
    }
}
