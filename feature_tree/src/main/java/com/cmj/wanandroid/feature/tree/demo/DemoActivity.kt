package com.cmj.wanandroid.feature.tree.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cmj.wanandroid.feature.tree.R
import com.cmj.wanandroid.feature.tree.TreeFragment
import com.cmj.wanandroid.lib.base.AbsDemoActivity

class DemoActivity: AbsDemoActivity() {

    override fun createFragment(): Fragment {
        return TreeFragment()
    }

}