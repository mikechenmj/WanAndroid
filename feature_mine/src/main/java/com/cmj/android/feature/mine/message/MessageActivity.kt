package com.cmj.android.feature.mine.message

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cmj.android.feature.mine.R
import com.cmj.android.feature.mine.databinding.ActivityMessageBinding
import com.cmj.wanandroid.lib.base.BaseActivity
import com.cmj.wanandroid.lib.base.ChildFragment
import com.cmj.wanandroid.lib.base.ui.ScaleInTransformer
import com.cmj.wanandroid.lib.base.ui.TabMediator

class MessageActivity : BaseActivity<ViewModel, ActivityMessageBinding>() {

    companion object {
        private val CHILD_FRAGMENTS = arrayOf(
            ChildFragment(R.string.message_unread_label, UnReadMessageFragment::class.java),
            ChildFragment(R.string.message_read_label, ReadMessageFragment::class.java)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSupportActionBar(binding.toolbar)
        binding.messagePager.apply {
            adapter = MessageAdapter()
            val recycler = getChildAt(0) as? RecyclerView
            recycler?.layoutManager?.isItemPrefetchEnabled = false
            setPageTransformer(ScaleInTransformer())
            offscreenPageLimit = 1
        }

        TabMediator(binding.tabLayout, binding.messagePager, true) { tab, position ->
            tab.text = getString(CHILD_FRAGMENTS[position].titleRes)
        }.attach()
    }

    inner class MessageAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return CHILD_FRAGMENTS.size
        }

        override fun createFragment(position: Int): Fragment {
            return CHILD_FRAGMENTS[position].clazz.newInstance()
        }
    }

}