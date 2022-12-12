package com.cmj.wanandroid.content.project

import android.view.View
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.databinding.FragmentProjectBinding
import com.cmj.wanandroid.content.AbsContentFragment
import com.cmj.wanandroid.ui.TabMediator
import com.google.android.material.tabs.TabLayout

class ProjectFragment: AbsContentFragment<ViewModel, ViewModel, FragmentProjectBinding>() {

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
    }
}