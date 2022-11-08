package com.cmj.wanandroid.kt

import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment

fun FragmentActivity.findNavigationById(id: Int): NavHostFragment? {
    return supportFragmentManager.findFragmentById(id) as? NavHostFragment
}