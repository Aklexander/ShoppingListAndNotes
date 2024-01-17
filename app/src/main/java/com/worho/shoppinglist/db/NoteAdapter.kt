package com.worho.shoppinglist.db

import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worho.shoppinglist.R
import com.worho.shoppinglist.databinding.NoteListItemBinding
import com.worho.shoppinglist.entities.NoteItem
import com.worho.shoppinglist.utils.HtmlManager
import com.worho.shoppinglist.utils.TimeManager


class NoteAdapter(private val listener: Listener, private val preferences: SharedPreferences) : ListAdapter<NoteItem , NoteAdapter.ItemHolder>(ItemComparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener, preferences)
    }


    class ItemHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = NoteListItemBinding.bind(view)

        fun setData(note: NoteItem, listener: Listener, preferences: SharedPreferences) = with(binding){
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text =TimeManager.getTimeFormat(note.time, preferences)
            itemView.setOnClickListener {
                listener.onClickItem(note)
            }
            imDelete.setOnClickListener {
                listener.deleteItem(note.id!!)
            }
        }
        companion object{
            fun create(parent: ViewGroup): ItemHolder{
                return ItemHolder(
                    LayoutInflater.from(parent.context).
                    inflate(R.layout.note_list_item, parent, false)
                )
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<NoteItem>(){
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return newItem == oldItem
        }
    }

    interface Listener{
        fun deleteItem(id: Int)
       fun onClickItem(note: NoteItem)
    }
}