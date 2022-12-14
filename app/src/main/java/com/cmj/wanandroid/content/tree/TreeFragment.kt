package com.cmj.wanandroid.content.tree

import android.view.View
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.content.AbsContentFragment
import com.cmj.wanandroid.databinding.FragmentTreeBinding
import com.cmj.wanandroid.ui.TabMediator
import com.google.android.material.tabs.TabLayout

class TreeFragment: AbsContentFragment<ViewModel, ViewModel, FragmentTreeBinding>() {

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
    }
}