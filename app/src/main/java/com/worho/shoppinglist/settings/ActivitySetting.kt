package com.worho.shoppinglist.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.worho.shoppinglist.R

class ActivitySetting : AppCompatActivity() {
    lateinit var preferences: SharedPreferences
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedThem())
        setContentView(R.layout.activity_setting)

        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.placeHolder,
                    FragmentSettings()
                ).commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getSelectedThem(): Int{
        return when(preferences.getString("chose_theme_key", "blue")){
            "blue" -> R.style.Theme_ShoppingListBlue
            "green" -> R.style.Theme_ShoppingListGreen
            else -> R.style.Theme_ShoppingListBlue
        }
    }
}