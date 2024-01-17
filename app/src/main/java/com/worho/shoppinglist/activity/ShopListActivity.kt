package com.worho.shoppinglist.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.worho.shoppinglist.R
import com.worho.shoppinglist.databinding.ActivityShopListBinding
import com.worho.shoppinglist.db.MainViewModel
import com.worho.shoppinglist.db.ShoppingItemAdapter
import com.worho.shoppinglist.dialog.EditItemDialog
import com.worho.shoppinglist.entities.LibraryItem
import com.worho.shoppinglist.entities.ShoppingListItem
import com.worho.shoppinglist.entities.ShoppingListName
import com.worho.shoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShoppingItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding
    private var shoppingListName: ShoppingListName? = null
    private lateinit var saveItem: MenuItem
    private var editTextMenu: EditText? = null
    private var adapter: ShoppingItemAdapter? = null
    private lateinit var textWatcher: TextWatcher
    lateinit var preferences: SharedPreferences


    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedThem())
        setContentView(binding.root)
        initRcShopItem()
        init()
        shopItemsObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_shop_item_menu, menu)
        saveItem = menu?.findItem(R.id.menu_save_item)!!
        val newItem = menu.findItem(R.id.menu_new_item)
        editTextMenu = newItem.actionView?.findViewById(R.id.editTextText) as EditText
        newItem.setOnActionExpandListener(expandActionMenu())
        saveItem.isVisible = false
        textWatcher = textWatcherFun()
        return true
    }

    private fun textWatcherFun(): TextWatcher{
        return object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("MyLog", "text: $p0")
                mainViewModel.getAllLibraryItems("%$p0%")
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_save_item -> addNewShopItem(editTextMenu?.text.toString())
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shoppingListName?.id!!, true)
                finish()
            }
            R.id.clear_list -> mainViewModel.deleteShopList(shoppingListName?.id!!, false)
            R.id.share_list -> shareListMenu()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun shareListMenu(){
        startActivity(Intent.createChooser(
            ShareHelper.shareShopList(adapter?.currentList!!, shoppingListName?.name!!),
            ContextCompat.getString(this, R.string.share_list_item)
        ))
    }

    private fun addNewShopItem(name: String){
        if (name.isEmpty())return
        val shopItem = ShoppingListItem(
            null,
            name,
            null,
            false,
            shoppingListName?.id!!,
            0
        )
        editTextMenu?.setText("")
        mainViewModel.insertShopItem(shopItem)
    }

    private fun expandActionMenu(): MenuItem.OnActionExpandListener{
        return object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                saveItem.isVisible = true
                editTextMenu?.addTextChangedListener(textWatcher)
                libraryItemsObserver()
                mainViewModel.getAllShopItems(shoppingListName?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                saveItem.isVisible = false
                invalidateMenu()
                editTextMenu?.removeTextChangedListener(textWatcher)
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                editTextMenu?.setText("")
                shopItemsObserver()
                return true
            }

        }
    }

    private fun shopItemsObserver(){
        mainViewModel.getAllShopItems(shoppingListName?.id!!).observe(this){
            adapter?.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()){
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun libraryItemsObserver(){
        mainViewModel.libraryItems.observe(this) {
            val tempList = ArrayList<ShoppingListItem>()
            it.forEach {  libItem ->
                val shopItem = ShoppingListItem(
                    libItem.id,
                    libItem.name,
                    "",
                    false,
                    1,
                    1
                )
                tempList.add(shopItem)
            }
            adapter?.submitList(tempList)
        }
    }

    private fun init(){
        shoppingListName = intent.getSerializableExtra(SHOP_LIST_NAME) as ShoppingListName
    }

    private fun initRcShopItem(){
        binding.rcShopItem.layoutManager = LinearLayoutManager(this@ShopListActivity)
        adapter = ShoppingItemAdapter(this)
        binding.rcShopItem.adapter = adapter
    }

    private fun editShopItemDialog(shopItem: ShoppingListItem){
        EditItemDialog.editItemDialog(this, shopItem, object : EditItemDialog.Listener{
            override fun onClick(shopItem: ShoppingListItem) {
                mainViewModel.updateShopItem(shopItem)
            }

        })
    }
    private fun editLibraryItemDialog(shopItem: ShoppingListItem){
        EditItemDialog.editItemDialog(this, shopItem, object : EditItemDialog.Listener{
            override fun onClick(shopItem: ShoppingListItem) {
                mainViewModel.updateLibrary(LibraryItem(id = shopItem.id, name = shopItem.name))
                mainViewModel.getAllLibraryItems("%${editTextMenu?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0
        adapter?.currentList?.forEach {
            if(it.itemChecked) checkedItemCounter++
        }
        val tempShopListNameItem = shoppingListName?.copy(
            allItemCounter = adapter?.itemCount!!,
            checkedItemCounter = checkedItemCounter
        )
        mainViewModel.updateShopListName(tempShopListNameItem!!)
    }

    override fun onClick(shopItem: ShoppingListItem, status: Int) {
        when(status){
            ShoppingItemAdapter.CHECK_ITEM -> mainViewModel.updateShopItem(shopItem)
            ShoppingItemAdapter.EDIT_ITEM -> editShopItemDialog(shopItem)
            ShoppingItemAdapter.LIBRARY_EDIT_ITEM -> editLibraryItemDialog(shopItem)
            ShoppingItemAdapter.ADD_ITEM -> addNewShopItem(shopItem.name)
            ShoppingItemAdapter.LIBRARY_DELETE_ITEM -> {
                mainViewModel.deleteLibraryItem(shopItem.id!!)
                mainViewModel.getAllLibraryItems("%${editTextMenu?.text.toString()}%")
            }
        }
    }
    private fun getSelectedThem(): Int{
        return when(preferences.getString("chose_theme_key", "blue")){
            "blue" -> R.style.Theme_ShoppingListBlue
            "green" -> R.style.Theme_ShoppingListGreen
            else -> R.style.Theme_ShoppingListBlue
        }
    }

    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }

}