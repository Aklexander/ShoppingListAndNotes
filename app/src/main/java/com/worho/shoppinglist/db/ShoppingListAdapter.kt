package com.worho.shoppinglist.db

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worho.shoppinglist.R
import com.worho.shoppinglist.databinding.ShopListNameItemBinding
import com.worho.shoppinglist.entities.ShoppingListName

class ShoppingListAdapter(private val listener: Listener): ListAdapter<ShoppingListName, ShoppingListAdapter.ShoppingListHolder>(ItemComparator()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListHolder {
        return ShoppingListHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ShoppingListAdapter.ShoppingListHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }

    class ShoppingListHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ShopListNameItemBinding.bind(view)

        fun setData(shopList: ShoppingListName, listener: Listener) = with(binding){
            val counterText = "${shopList.checkedItemCounter}/${shopList.allItemCounter}"
            val colorState = ColorStateList.valueOf(getColorState(
                shopList, binding.root.context
            ))
            tvNameShop.text = shopList.name
            tvTime.text  = shopList.time
            tvConter.text = counterText
            progressBar.max = shopList.allItemCounter
            progressBar.progress = shopList.checkedItemCounter
            progressBar.progressTintList = colorState
            cardCounter.backgroundTintList = colorState
            imDelete.setOnClickListener {
                listener.deleteItem(shopList.id!!)
            }
            imEdit.setOnClickListener {
                listener.updateShopListName(shopList)
            }
            itemView.setOnClickListener {
                listener.onClick(shopList)
            }
        }

        private fun getColorState(shopList: ShoppingListName, context: Context): Int{
            return if(shopList.allItemCounter == shopList.checkedItemCounter) {
                ContextCompat.getColor(context, R.color.picker_green)
            } else {
                ContextCompat.getColor(context, R.color.picker_red)
            }
        }

        companion object{
            fun create(parent: ViewGroup): ShoppingListHolder{
                return ShoppingListHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_list_name_item, parent, false)
                )
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<ShoppingListName>(){
        override fun areItemsTheSame(
            oldItem: ShoppingListName,
            newItem: ShoppingListName
        ): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(
            oldItem: ShoppingListName,
            newItem: ShoppingListName
        ): Boolean {
            return newItem == oldItem
        }

    }

    interface Listener{
        fun deleteItem(id: Int)
        fun updateShopListName(shopListName: ShoppingListName)
        fun onClick(shopListName: ShoppingListName)
    }
}