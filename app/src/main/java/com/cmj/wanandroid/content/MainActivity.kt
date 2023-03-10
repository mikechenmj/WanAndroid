package com.cmj.wanandroid.content

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.addRepeatingJob
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.cmj.wanandroid.R
import com.cmj.wanandroid.lib.base.BaseActivity
import com.cmj.wanandroid.feature.home.HomeFragment
import com.cmj.wanandroid.feature.project.ProjectFragment
import com.cmj.wanandroid.feature.tree.TreeFragment
import com.cmj.wanandroid.feature.wx.WxArticleFragment
import com.cmj.wanandroid.databinding.ActivityMainBinding
import com.cmj.wanandroid.lib.base.ui.ICollapsingHolder
import com.cmj.wanandroid.lib.base.ui.ITabLayoutHolder
import com.cmj.wanandroid.lib.base.ui.ScaleInTransformer
import com.cmj.wanandroid.feature.mine.MineFragment
import com.cmj.wanandroid.feature.search.SearchViewModel
import com.cmj.wanandroid.lib.base.Constant
import com.cmj.wanandroid.lib.base.kt.getOrHandleError
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collect

class MainActivity : BaseActivity<SearchViewModel, ActivityMainBinding>(), ICollapsingHolder,
    ITabLayoutHolder {

    companion object {
        private val CHILD_FRAGMENTS = arrayOf(
            ChildFragment(R.string.home_label, HomeFragment::class.java),
            ChildFragment(R.string.tree_label, TreeFragment::class.java),
            ChildFragment(R.string.wx_official_label, WxArticleFragment::class.java),
            ChildFragment(R.string.project_label, ProjectFragment::class.java),
            ChildFragment(R.string.mine_label, MineFragment::class.java),
        )
    }

    data class ChildFragment(@StringRes val titleRes: Int, val clazz: Class<out Fragment>)

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNav.apply {
            setOnNavigationItemSelectedListener {
                binding.contentPager.setCurrentItem(it.order, false)
                true
            }
        }

        binding.contentPager.apply {
            adapter = ContentAdapter()
            val recycler = getChildAt(0) as? RecyclerView
            recycler?.layoutManager?.isItemPrefetchEnabled = false
            setPageTransformer(ScaleInTransformer())
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.bottomNav.menu.apply {
                        forEach {
                            it.isChecked = false
                        }
                        get(position).isChecked = true
                    }
                }
            })
        }

        binding.search.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, com.cmj.wanandroid.feature.search.SearchActivity::class.java).apply {
                    putExtra(Constant.Search.EXTRA_SEARCH_HOTKEY, text.toString())
                })
            }
            addRepeatingJob(Lifecycle.State.STARTED) {
                viewModel.hotKeyFlow.collect {
                    val hotkeys = it.getOrHandleError(this@MainActivity) ?: return@collect
                    text = hotkeys.random().name
                }
            }
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

    inner class ContentAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return CHILD_FRAGMENTS.size
        }

        override fun createFragment(position: Int): Fragment {
            return CHILD_FRAGMENTS[position].clazz.newInstance()
        }
    }
}

