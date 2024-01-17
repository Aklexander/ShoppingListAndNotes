package com.worho.shoppinglist.db

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worho.shoppinglist.R
import com.worho.shoppinglist.databinding.RcShopLibraryItemBinding
import com.worho.shoppinglist.databinding.RcShopListItemBinding
import com.worho.shoppinglist.databinding.ShopListNameItemBinding
import com.worho.shoppinglist.entities.ShoppingListItem
import com.worho.shoppinglist.entities.ShoppingListName

class ShoppingItemAdapter(private val listener: Listener): ListAdapter<ShoppingListItem, ShoppingItemAdapter.ShoppingItemHolder>(ItemComparator()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingItemHolder {
        return if(viewType == 0) {
            ShoppingItemHolder.createItem(parent)
        }else {
            ShoppingItemHolder.createLibraryItem(parent)
        }
    }

    override fun onBindViewHolder(holder: ShoppingItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setDataItem(getItem(position), listener)
        }else{
            holder.setLibraryItemData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ShoppingItemHolder(val view: View): RecyclerView.ViewHolder(view){
        fun setDataItem(shopList: ShoppingListItem, listener: Listener){
            val binding = RcShopListItemBinding.bind(view)
            binding.apply {
                tvNameItem.text = shopList.name
                tvDescItem.text = shopList.itemInfo
                tvDescItem.visibility = infoVisibility(shopList)
                checkBoxItem.isChecked = shopList.itemChecked
                setPaintFlagAndColor(binding)
                checkBoxItem.setOnClickListener {
                    listener.onClick(shopList.copy(itemChecked = checkBoxItem.isChecked), CHECK_ITEM)
                }
                imEditRc.setOnClickListener {
                    listener.onClick(shopList, EDIT_ITEM)
                }
            }
        }
        fun setLibraryItemData(shopList: ShoppingListItem, listener: Listener){
            val binding = RcShopLibraryItemBinding.bind(view)
            binding.apply {
                tvNameLibItem.text = shopList.name
                imEditRcLib.setOnClickListener {
                    listener.onClick(shopList, LIBRARY_EDIT_ITEM)
                }
                imDeletRcLib.setOnClickListener {
                    listener.onClick(shopList, LIBRARY_DELETE_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClick(shopList, ADD_ITEM)
                }
            }

        }

        private fun setPaintFlagAndColor(binding: RcShopListItemBinding){
            binding.apply {
                if (checkBoxItem.isChecked) {
                    tvNameItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvDescItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvNameItem.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.grey
                        )
                    )
                    tvDescItem.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.grey
                        )
                    )
                } else {
                    tvNameItem.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvDescItem.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvNameItem.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    tvDescItem.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
            }
        }

        private fun infoVisibility(shopList: ShoppingListItem): Int{
            return if (shopList.itemInfo.isNullOrEmpty()){
                View.GONE
            } else {
                View.VISIBLE}
        }

        companion object{
            fun createItem(parent: ViewGroup): ShoppingItemHolder{
                return ShoppingItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.rc_shop_list_item, parent, false)
                )
            }
            fun createLibraryItem(parent: ViewGroup): ShoppingItemHolder{
                return ShoppingItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.rc_shop_library_item, parent, false)
                )
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<ShoppingListItem>(){
        override fun areItemsTheSame(
            oldItem: ShoppingListItem,
            newItem: ShoppingListItem
        ): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(
            oldItem: ShoppingListItem,
            newItem: ShoppingListItem
        ): Boolean {
            return newItem == oldItem
        }

    }

    interface Listener{
        fun onClick(shopItem: ShoppingListItem, status: Int)
    }

    companion object{
        const val CHECK_ITEM = 0
        const val EDIT_ITEM = 1
        const val LIBRARY_EDIT_ITEM = 2
        const val LIBRARY_DELETE_ITEM = 3
        const val ADD_ITEM = 4

    }
}