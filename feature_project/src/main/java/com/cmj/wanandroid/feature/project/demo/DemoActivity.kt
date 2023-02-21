package com.cmj.wanandroid.feature.project.demo

import androidx.fragment.app.Fragment
import com.cmj.wanandroid.feature.project.ProjectFragment
import com.cmj.wanandroid.lib.base.AbsDemoActivity

class DemoActivity: AbsDemoActivity() {

    override fun createFragment(): Fragment {
        return ProjectFragment()
    }

}