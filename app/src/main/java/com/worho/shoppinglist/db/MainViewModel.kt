package com.worho.shoppinglist.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.worho.shoppinglist.entities.LibraryItem
import com.worho.shoppinglist.entities.NoteItem
import com.worho.shoppinglist.entities.ShoppingListItem
import com.worho.shoppinglist.entities.ShoppingListName
import kotlinx.coroutines.launch


class MainViewModel(dataBase: MainDataBase): ViewModel() {
    private val dao = dataBase.getDao()
    //работа с блокнотом
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()

    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }

    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }
    // работа со списками покупок
    val allShopListNames: LiveData<List<ShoppingListName>> = dao.getAllShoppingListNames().asLiveData()

    fun insertShopListName(shopListName: ShoppingListName) = viewModelScope.launch {
        dao.insertShopList(shopListName)
    }

    fun deleteShopList(id: Int, deleteName: Boolean) = viewModelScope.launch {
        if(deleteName) dao.deleteShopListName(id)
        dao.deleteShopItems(id)
    }

    fun updateShopListName(shopListName: ShoppingListName) = viewModelScope.launch{
        dao.updateShopListNames(shopListName)
    }
    // работа с предметами покупок
    fun getAllShopItems(listId: Int): LiveData<List<ShoppingListItem>>{
        return dao.getAllShopItems(listId).asLiveData()
    }
    fun insertShopItem(shopItem: ShoppingListItem) = viewModelScope.launch {
        dao.insertShopItem(shopItem)
        if (getLibraryItem(shopItem.name)) dao.insertLibraryItem(
            LibraryItem(
                null,
                shopItem.name
            )
        )
    }
    fun updateShopItem(shopItem: ShoppingListItem)= viewModelScope.launch {
        dao.updateShopItem(shopItem)
    }

    val libraryItems = MutableLiveData<List<LibraryItem>>()
    private suspend fun getLibraryItem(name: String): Boolean{
        return dao.getLibraryItems(name).isEmpty()
    }
    fun getAllLibraryItems(name:String) = viewModelScope.launch {
        libraryItems.postValue(dao.getLibraryItems(name))
    }
    fun updateLibrary(libraryItem: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(libraryItem)
    }
    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }


    //немного устарело, но надо запомнить
    class MainViewModelFactory(val dataBase: MainDataBase): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")

        }
    }
}