package com.worho.shoppinglist.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeManager {
    private const val DEF_TIME_FORMATE = "hh:mm dd/MM/yyyy"
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String{
        val formatter = SimpleDateFormat(DEF_TIME_FORMATE, Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, pref: SharedPreferences): String{
        val defFormatter = SimpleDateFormat(DEF_TIME_FORMATE, Locale.getDefault())
        val defData = defFormatter.parse(time)
        val newFormat = pref.getString("pref_chose_time_format", DEF_TIME_FORMATE)
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())
        return if (defData != null){
            newFormatter.format(defData)
        } else{
            time
        }
    }


}