package com.worho.shoppinglist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.worho.shoppinglist.entities.LibraryItem
import com.worho.shoppinglist.entities.NoteItem
import com.worho.shoppinglist.entities.ShoppingListItem
import com.worho.shoppinglist.entities.ShoppingListName

@Database (entities = [LibraryItem::class, NoteItem::class,
    ShoppingListItem::class, ShoppingListName::class], version = 1)
abstract class MainDataBase: RoomDatabase() {
    abstract fun getDao():Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null
        fun getDataBase(context: Context): MainDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list.db"
                ).build()
                instance
            }
        }
    }
}