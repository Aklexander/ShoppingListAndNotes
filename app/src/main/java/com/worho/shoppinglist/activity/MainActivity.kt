package com.worho.shoppinglist.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.worho.shoppinglist.R
import com.worho.shoppinglist.biling.BillingManager
import com.worho.shoppinglist.databinding.ActivityMainBinding
import com.worho.shoppinglist.dialog.NewListDialog
import com.worho.shoppinglist.fragments.FragmentManager
import com.worho.shoppinglist.fragments.NoteFragment
import com.worho.shoppinglist.fragments.ShopListNameFragment
import com.worho.shoppinglist.settings.ActivitySetting

class MainActivity : AppCompatActivity(), NewListDialog.Listener {

    lateinit var binding: ActivityMainBinding
    lateinit var preferences: SharedPreferences
    private var currentMenuItemId = R.id.shop_list
    private var currentThem = ""
    private var interADS: InterstitialAd? = null
    private var countAdShow = 0
    private val countAdShowMax = 3
    private lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedThem())
        currentThem = preferences.getString("chose_theme_key", "blue").toString()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNameFragment.newInstance(), this)
        setBottomNavListener()
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        if(!pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false))loadInterADS()
    }

    private fun loadInterADS(){
        val request = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.app_inter_ads_id), request,
            object :InterstitialAdLoadCallback(){
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("MyLog","ad = $ad")
                    interADS = ad
                }

                override fun onAdFailedToLoad(lError: LoadAdError) {
                    val text = lError.message
                    Log.d("MyLog","ad = error $lError ")
                    Log.d("MyLog","ad = error text $text ")
                    interADS = null
                }
            })
    }

    private fun showInterADS(adListener: AdListener){
        if (interADS != null ){
            Log.d("MyLog","ad = $interADS")

            interADS?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    interADS = null
                    loadInterADS()
                    countAdShow = 0
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interADS = null
                    loadInterADS()
                }

                override fun onAdShowedFullScreenContent() {
                    interADS = null
                    loadInterADS()
                }
            }
            interADS?.show(this)
        } else{
            Log.d("MyLog","open finish")

            countAdShow++
            loadInterADS()
            adListener.onFinish()
        }

    }


    private fun setBottomNavListener(){
        binding.bottonNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.setting -> {
                    showInterADS(object : AdListener{
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, ActivitySetting::class.java))
                        }
                    })
                }
                R.id.notes_list -> {
                    currentMenuItemId = R.id.notes_list
                    showInterADS(object : AdListener{
                        override fun onFinish() {
                            FragmentManager.setFragment(NoteFragment.newInstance(), this@MainActivity)
                        }
                    })
                }
                R.id.shop_list -> {
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNameFragment.newInstance(), this)
                }
                R.id.new_item -> {
                    FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottonNav.selectedItemId = currentMenuItemId
        if (preferences.getString("chose_theme_key", "blue") != currentThem) recreate()
    }

    private fun getSelectedThem(): Int{
        return when(preferences.getString("chose_theme_key", "blue")){
            "blue" -> R.style.Theme_ShoppingListBlue
            "green" -> R.style.Theme_ShoppingListGreen
            else -> R.style.Theme_ShoppingListBlue
        }
    }

    override fun onClick(name: String) {
        Log.d("MyLog", "Name: $name")
    }

    interface AdListener{
        fun onFinish()
    }
}