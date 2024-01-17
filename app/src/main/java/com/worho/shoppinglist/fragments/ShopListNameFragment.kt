package com.worho.shoppinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.worho.shoppinglist.activity.MainApp
import com.worho.shoppinglist.activity.ShopListActivity
import com.worho.shoppinglist.databinding.FragmentShopListNameBinding
import com.worho.shoppinglist.db.MainViewModel
import com.worho.shoppinglist.db.ShoppingListAdapter
import com.worho.shoppinglist.dialog.DeleteDialog
import com.worho.shoppinglist.dialog.NewListDialog
import com.worho.shoppinglist.entities.ShoppingListName
import com.worho.shoppinglist.utils.TimeManager

class ShopListNameFragment : BaseFragment(), ShoppingListAdapter.Listener{
    private lateinit var binding: FragmentShopListNameBinding
    private lateinit var adapter: ShoppingListAdapter

    private val mainViewModel:MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{
            override fun onClick(name: String) {
                val shopListName = ShoppingListName(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShopListNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView(){
        binding.rcViewShopList.layoutManager = LinearLayoutManager(activity)
        adapter = ShoppingListAdapter(this@ShopListNameFragment)
        binding.rcViewShopList.adapter = adapter

    }

    private fun observer(){
        mainViewModel.allShopListNames.observe(viewLifecycleOwner){
            adapter.submitList(it)
            binding.tvInfo.visibility = if (it.isEmpty()){
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = ShopListNameFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.deleteDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }

        })
    }

    override fun updateShopListName(shopListName: ShoppingListName) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{
            override fun onClick(name: String) {
                mainViewModel.updateShopListName(shopListName.copy(name = name))
            }

        }, shopListName.name)
    }

    override fun onClick(shopListName: ShoppingListName) {
        val intent = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListName)
        }
        startActivity(intent)
    }
}