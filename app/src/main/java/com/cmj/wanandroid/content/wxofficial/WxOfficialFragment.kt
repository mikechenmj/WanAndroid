package com.cmj.wanandroid.content.wxofficial

import android.view.View
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.databinding.FragmentWxOfficialBinding
import com.cmj.wanandroid.content.AbsContentFragment
import com.cmj.wanandroid.ui.TabMediator
import com.google.android.material.tabs.TabLayout

class WxOfficialFragment: AbsContentFragment<ViewModel, ViewModel, FragmentWxOfficialBinding>() {

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
    }
}