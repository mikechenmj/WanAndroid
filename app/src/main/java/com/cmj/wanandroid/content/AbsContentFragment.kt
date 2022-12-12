package com.cmj.wanandroid.content

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.base.BaseFragment
import com.cmj.wanandroid.ui.TabMediator
import com.google.android.material.tabs.TabLayout

abstract class AbsContentFragment<VM : ViewModel, AVM : ViewModel, VB : ViewBinding> : BaseFragment<VM, AVM, VB>(),
    ITabLayoutHolder, ICollapsingHolder {

    abstract fun initTabMediator(tabLayout: TabLayout?): TabMediator?

    abstract fun getCollapsingView(): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tabLayoutMediator: TabMediator? = null
        viewLifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        val tabLayout = getTabLayout()
                        tabLayoutMediator = initTabMediator(tabLayout)
                        val collapsingView = getCollapsingView()
                        tabLayoutMediator?.attach()
                        tabLayout?.isVisible = tabLayoutMediator != null

                        if (collapsingView != null) {
                            setCollapsingView(collapsingView)
                        } else {
                            clearCollapsingView()
                        }
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        tabLayoutMediator?.detach()
                    }
                }
            }
        })
    }

    override fun getCollapsingContainer(): View? {
        return (activity as? ICollapsingHolder)?.getCollapsingContainer()
    }

    override fun addCollapsingView(view: View): Boolean {
        val viewHolder = activity as? ICollapsingHolder ?: return false
        return viewHolder.addCollapsingView(view)
    }

    override fun setCollapsingView(view: View): Boolean {
        val viewHolder = activity as? ICollapsingHolder ?: return false
        return viewHolder.setCollapsingView(view)
    }

    override fun clearCollapsingView(): Boolean {
        val viewHolder = activity as? ICollapsingHolder ?: return false
        return viewHolder.clearCollapsingView()
    }

    override fun setCollapsingExpanded(expanded: Boolean, animate: Boolean) {
        val viewHolder = activity as? ICollapsingHolder ?: return
        return viewHolder.setCollapsingExpanded(expanded, animate)
    }

    override fun getTabLayout(): TabLayout? {
        return (activity as? ITabLayoutHolder)?.getTabLayout()
    }
}