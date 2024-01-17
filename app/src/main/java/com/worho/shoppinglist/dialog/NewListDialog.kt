package com.worho.shoppinglist.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.worho.shoppinglist.R
import com.worho.shoppinglist.databinding.NewListDialogBinding

object NewListDialog {
    fun showDialog(context: Context, listener: Listener, name: String){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            etNewNameList.setText(name)
            if (name.isNotEmpty()){
                tvTitle.text =context.getText( R.string.update_list)
                btNewDialogList.text = context.getText(R.string.update_new_list)
            }
            btNewDialogList.setOnClickListener {
                val listName = etNewNameList.text.toString()
                if (listName.isNotEmpty()) {
                    listener.onClick(listName)
                }
                dialog?.dismiss()
            }
        }

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }

    interface Listener{
        fun onClick(name: String)
    }
}