package com.cmj.wanandroid.content.mine

import android.view.View
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.databinding.FragmentMineBinding
import com.cmj.wanandroid.content.AbsContentFragment
import com.cmj.wanandroid.ui.TabMediator
import com.google.android.material.tabs.TabLayout

class MineFragment: AbsContentFragment<ViewModel, ViewModel, FragmentMineBinding>() {

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
    }
}