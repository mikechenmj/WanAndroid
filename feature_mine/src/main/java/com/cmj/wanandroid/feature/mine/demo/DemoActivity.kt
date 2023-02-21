package com.cmj.wanandroid.feature.mine.demo

import androidx.fragment.app.Fragment
import com.cmj.wanandroid.feature.mine.MineFragment
import com.cmj.wanandroid.lib.base.AbsDemoActivity

class DemoActivity: AbsDemoActivity() {

    override fun createFragment(): Fragment {
        return MineFragment()
    }

}