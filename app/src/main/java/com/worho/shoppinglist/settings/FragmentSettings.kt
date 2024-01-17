package com.worho.shoppinglist.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.worho.shoppinglist.R
import com.worho.shoppinglist.biling.BillingManager

class FragmentSettings: PreferenceFragmentCompat() {
    private lateinit var removeAdPref: Preference
    private lateinit var billingManager: BillingManager
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        init()
    }
    private fun init(){
        billingManager = BillingManager(activity as AppCompatActivity)
        removeAdPref = findPreference<Preference>("remove_ads_key")!!
        removeAdPref.setOnPreferenceClickListener {
            Log.d("MyLog", "remove ads pref")
            billingManager.startConnection()
            true
        }
    }

    override fun onDestroy() {
        billingManager.closeConnection()
        super.onDestroy()
    }
}