package com.worho.shoppinglist.utils

import android.content.Intent
import com.worho.shoppinglist.entities.ShoppingListItem

object ShareHelper {
    fun shareShopList(shopList: List<ShoppingListItem>, shopName: String): Intent{
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeSharpText(shopList, shopName))
        }
        return intent
    }

    private fun makeSharpText(shopList: List<ShoppingListItem>, shopName: String): String{
        var sBuilder = StringBuilder()
        sBuilder.append("<<$shopName>>")
        sBuilder.append("\n")
        var count = 0
        shopList.forEach {
            val itemInfo = if(it.itemInfo.isNullOrEmpty()) "" else {
                it.itemInfo.toString()
            }
            sBuilder.append("${++count} - ${it.name} ${itemInfo}")
            sBuilder.append("\n")
        }
        return sBuilder.toString()

    }
}