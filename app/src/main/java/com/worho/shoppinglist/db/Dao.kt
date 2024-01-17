package com.worho.shoppinglist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.worho.shoppinglist.entities.LibraryItem
import com.worho.shoppinglist.entities.NoteItem
import com.worho.shoppinglist.entities.ShoppingListItem
import com.worho.shoppinglist.entities.ShoppingListName
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Insert
    suspend fun insertNote(note: NoteItem)
    @Update
    suspend fun updateNote(note:NoteItem)
    @Query ("SELECT * FROM note_item")
    fun getAllNotes(): Flow<List<NoteItem>>
    @Query("DELETE FROM note_item WHERE id IS :id")
    suspend fun deleteNote(id: Int)

    @Insert
    suspend fun insertShopList(shopListName: ShoppingListName)
    @Update
    suspend fun updateShopListNames(shopListName: ShoppingListName)
    @Query ("SELECT * FROM shopping_list_names")
    fun getAllShoppingListNames(): Flow<List<ShoppingListName>>
    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)

    @Insert
    suspend fun insertShopItem(shopItem: ShoppingListItem)
    @Query("SELECT * FROM shop_list_item WHERE listId LIKE :listId")
    fun getAllShopItems(listId: Int): Flow<List<ShoppingListItem>>
    @Update
    suspend fun updateShopItem(shopItem: ShoppingListItem)
    @Query("DELETE FROM shop_list_item WHERE listId LIKE :listId")
    suspend fun deleteShopItems(listId: Int)

    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)
    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getLibraryItems(name: String): List<LibraryItem>
    @Update
    suspend fun updateLibraryItem(libraryItem: LibraryItem)
    @Query("DELETE FROM library WHERE id LIKE :id")
    suspend fun deleteLibraryItem(id: Int)
}