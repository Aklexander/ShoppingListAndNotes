package com.worho.shoppinglist.fragments

import androidx.fragment.app.Fragment


abstract class BaseFragment: Fragment() {
    abstract fun onClickNew()
}