package com.worho.shoppinglist.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.worho.shoppinglist.databinding.DeleteShopListDialogBinding

object DeleteDialog {
    fun deleteDialog(context: Context, listener: Listener){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DeleteShopListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)

        binding.apply {
            bCancel.setOnClickListener {
                dialog?.dismiss()
            }
            bDelete.setOnClickListener {
                listener.onClick()
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }

    interface Listener{
        fun onClick()
    }
}