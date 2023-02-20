package com.cmj.wanandroid.feature.home.demo

import androidx.fragment.app.Fragment
import com.cmj.wanandroid.feature.home.HomeFragment
import com.cmj.wanandroid.lib.base.AbsDemoActivity

class DemoActivity: AbsDemoActivity() {

    override fun createFragment(): Fragment {
        return HomeFragment()
    }

}