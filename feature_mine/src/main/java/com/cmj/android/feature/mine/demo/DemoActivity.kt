package com.cmj.android.feature.mine.demo

import androidx.fragment.app.Fragment
import com.cmj.android.feature.mine.MineFragment
import com.cmj.wanandroid.lib.base.AbsDemoActivity

class DemoActivity: AbsDemoActivity() {

    override fun createFragment(): Fragment {
        return MineFragment()
    }

}