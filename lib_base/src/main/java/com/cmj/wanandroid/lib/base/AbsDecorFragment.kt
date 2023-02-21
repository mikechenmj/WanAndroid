package com.cmj.wanandroid.lib.base

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.lib.base.ui.ICollapsingHolder
import com.cmj.wanandroid.lib.base.ui.ITabLayoutHolder
import com.cmj.wanandroid.lib.base.ui.TabMediator
import com.google.android.material.tabs.TabLayout

abstract class AbsDecorFragment<VM : ViewModel, AVM : ViewModel, VB : ViewBinding> :
    BaseFragment<VM, AVM, VB>(), ITabLayoutHolder, ICollapsingHolder {

    abstract fun initTabMediator(tabLayout: TabLayout?): TabMediator?

    abstract fun getCollapsingView(): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tabLayoutMediator: TabMediator? = null
        var tabLayout: TabLayout? = null
        var collapsingView: View? = null
        viewLifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        tabLayout = getTabLayout()
                        tabLayoutMediator = initTabMediator(tabLayout)
                        collapsingView = getCollapsingView()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        if (tabLayoutMediator != null) {
                            if (!tabLayoutMediator!!.isAttached) {
                                tabLayoutMediator!!.attach()
                                tabLayout?.isVisible = true
                            }
                        }
                        if (collapsingView != null) {
                            setCollapsingView(collapsingView!!)
                            setCollapsingExpanded(expanded = true, animate = true)
                        }
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        if (requireActivity().lifecycle.currentState != Lifecycle.State.RESUMED) {
                            return
                        }
                        if (tabLayoutMediator != null) {
                            tabLayoutMediator!!.detach()
                            tabLayout?.isVisible = false
                        }
                        if (collapsingView != null) {
                            clearCollapsingView()
                        }
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
        (activity as? ICollapsingHolder)?.setCollapsingExpanded(expanded, animate)
    }

    override fun getTabLayout(): TabLayout? {
        return (activity as? ITabLayoutHolder)?.getTabLayout()
    }
}