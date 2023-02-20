package com.cmj.wanandroid.lib.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.lib.base.databinding.DemoMainBinding
import com.cmj.wanandroid.lib.base.ui.ICollapsingHolder
import com.cmj.wanandroid.lib.base.ui.ITabLayoutHolder
import com.google.android.material.tabs.TabLayout

abstract class AbsDemoActivity : BaseActivity<ViewModel, DemoMainBinding>(), ICollapsingHolder,
    ITabLayoutHolder {

    abstract fun createFragment(): Fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id)
        if (fragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, createFragment())
                .commit()
        }
    }

    override fun getCollapsingContainer(): View {
        return binding.collapsingContainer
    }

    override fun addCollapsingView(view: View): Boolean {
        binding.collapsingContainer.addView(view)
        return true
    }

    override fun setCollapsingView(view: View): Boolean {
        binding.collapsingContainer.removeAllViews()
        binding.collapsingContainer.addView(view)
        return true
    }

    override fun clearCollapsingView(): Boolean {
        binding.collapsingContainer.removeAllViews()
        return true
    }

    override fun setCollapsingExpanded(expanded: Boolean, animate: Boolean) {
        binding.appBar.setExpanded(expanded, animate)
    }

    override fun getTabLayout(): TabLayout {
        return binding.tabLayout
    }
}