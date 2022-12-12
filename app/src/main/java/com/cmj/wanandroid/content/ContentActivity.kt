package com.cmj.wanandroid.content

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.content.home.HomeFragment
import com.cmj.wanandroid.content.mine.MineFragment
import com.cmj.wanandroid.content.project.ProjectFragment
import com.cmj.wanandroid.content.tree.TreeFragment
import com.cmj.wanandroid.content.wxofficial.WxOfficialFragment
import com.cmj.wanandroid.databinding.ActivityContentBinding
import com.google.android.material.tabs.TabLayout

class ContentActivity : BaseActivity<ViewModel, ActivityContentBinding>(), ICollapsingHolder, ITabLayoutHolder {

    companion object {
        private val CHILD_FRAGMENTS = arrayOf(
            ChildFragment(R.string.home_label, HomeFragment::class.java),
            ChildFragment(R.string.tree_label, TreeFragment::class.java),
            ChildFragment(R.string.wx_official_label, WxOfficialFragment::class.java),
            ChildFragment(R.string.project_label, ProjectFragment::class.java),
            ChildFragment(R.string.mine_label, MineFragment::class.java),
        )
    }

    data class ChildFragment(@StringRes val titleRes: Int, val clazz: Class<out Fragment>)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNav.apply {
            itemIconTintList = null
            setOnNavigationItemSelectedListener {
                binding.contentPager.setCurrentItem(it.order, false)
                true
            }
        }
        binding.contentPager.apply {
            adapter = ContentAdapter()
            isUserInputEnabled = false
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
        binding.appBar.setExpanded(true, false)
    }

    override fun getTabLayout(): TabLayout {
        return binding.tabLayout
    }

    inner class ContentAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return CHILD_FRAGMENTS.size
        }

        override fun createFragment(position: Int): Fragment {
            return CHILD_FRAGMENTS[position].clazz.newInstance()
        }
    }
}

