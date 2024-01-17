package com.worho.shoppinglist.activity

import android.app.Application
import com.worho.shoppinglist.db.MainDataBase


class MainApp: Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}