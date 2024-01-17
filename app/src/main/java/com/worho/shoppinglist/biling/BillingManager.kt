package com.worho.shoppinglist.biling

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams

class BillingManager(val activity: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient(){
        billingClient = BillingClient
            .newBuilder(activity)
            .setListener(getPurchaseListener())
            .enablePendingPurchases()
            .build()
    }

    fun startConnection(){
        billingClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                getItem()
            }

        })
    }

    private fun savePref(isPurchase: Boolean){
        val pref = activity.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_KEY, isPurchase)
        editor.apply()
    }

    private fun getItem(){
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_AD_ITEM)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync( skuDetails.build()){
            bResult, list ->
            kotlin.run{
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK){
                        if (list != null) {
                            if (list.isNotEmpty()){
                                val bFlowParams = BillingFlowParams
                                    .newBuilder()
                                    .setSkuDetails(list[0])
                                    .build()
                                billingClient?.launchBillingFlow(activity, bFlowParams)
                            }
                        }
                }
        }
        }
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener {
        return PurchasesUpdatedListener {
                billingResult, list ->
            run {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase){
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
            if (!purchase.isAcknowledged){
                val acParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(acParams){
                    if (it.responseCode == BillingClient.BillingResponseCode.OK){
                        savePref(true)
                        Toast.makeText(activity, "Спасибо за покупку!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Не удалось произвести покупку!", Toast.LENGTH_LONG).show()
                        savePref(false)
                    }
                }
            }
        }
    }

    fun closeConnection(){
        billingClient?.endConnection()
    }

    companion object{
        const val REMOVE_AD_ITEM = "remove_ad_item"
        const val MAIN_PREF = "main_pref_key"
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}