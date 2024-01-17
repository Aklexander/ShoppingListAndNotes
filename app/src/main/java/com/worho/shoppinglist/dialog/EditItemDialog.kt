package com.worho.shoppinglist.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.worho.shoppinglist.databinding.DialogEditShopItemBinding
import com.worho.shoppinglist.entities.ShoppingListItem

object EditItemDialog {
    fun editItemDialog(context: Context,shopItem: ShoppingListItem, listener: Listener){
        var dialog: AlertDialog? = null
        val builder  = AlertDialog.Builder(context)
        val binding = DialogEditShopItemBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)

        binding.apply {
            edNameShopItem.setText(shopItem.name)
            edInfoShopItem.setText(shopItem.itemInfo)
            if (shopItem.itemType == 1) edInfoShopItem.visibility = View.GONE
            btUpdateShopItem.setOnClickListener {
                listener.onClick(shopItem.copy(name = edNameShopItem.text.toString(),
                    itemInfo = edInfoShopItem.text.toString()))
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }

    interface Listener{
        fun onClick(shopItem: ShoppingListItem)
    }
}