package com.cmj.wanandroid.feature.mine

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.addRepeatingJob
import com.alibaba.android.arouter.launcher.ARouter
import com.cmj.wanandroid.data.user.UserViewModel
import com.cmj.wanandroid.data.user.bean.User
import com.cmj.wanandroid.feature.mine.databinding.FragmentMineBinding
import com.cmj.wanandroid.feature.mine.databinding.UserInfoLayoutBinding
import com.cmj.wanandroid.feature.mine.message.MessageActivity
import com.cmj.wanandroid.feature.mine.private.PrivateArticleFragment
import com.cmj.wanandroid.feature.mine.star.StarArticleFragment
import com.cmj.wanandroid.lib.base.AbsDecorFragment
import com.cmj.wanandroid.lib.base.kt.getOrHandleError
import com.cmj.wanandroid.lib.base.router.RouterPath
import com.cmj.wanandroid.lib.base.ui.TabMediator
import com.cmj.wanandroid.lib.network.kt.getOrToastError
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MineFragment : AbsDecorFragment<ViewModel, UserViewModel, FragmentMineBinding>() {

    private lateinit var collapsingBinding: UserInfoLayoutBinding

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View {
        return initUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            refresh()
        }
        binding.login.setOnClickListener {
            ARouter.getInstance().build(RouterPath.ROUTER_LOGIN).navigation()
        }
        binding.logout.setOnClickListener {
            viewLifecycleScope.launch {
                showLoading()
                activityViewModel.logout().getOrToastError(requireContext())
                hideLoading()
                refresh()
            }
        }
        binding.userShared.setOnClickListener {
            PrivateArticleFragment.start(requireContext())
        }
        binding.userStared.setOnClickListener {
            StarArticleFragment.start(requireContext())
        }
        binding.userMessage.setOnClickListener {
            startActivity(Intent(requireContext(), MessageActivity::class.java))
        }
    }

    private suspend fun refresh() {
        val isLoggedIn = activityViewModel.isLoggedIn()
        binding.itemContainer.isVisible = isLoggedIn
        binding.login.isVisible = !isLoggedIn
        val info = if (isLoggedIn) {
            activityViewModel.userInfoAsync().await().getOrHandleError(requireContext())
        } else {
            null
        }
        collapsingBinding.refresh(info)
    }

    private fun initUserInfo(): View {
        val infoBinding = UserInfoLayoutBinding.inflate(
            LayoutInflater.from(requireContext()),
            view as ViewGroup?,
            false
        )
        collapsingBinding = infoBinding
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            activityViewModel.userInfoFlow.collect {
                val info = if (activityViewModel.isLoggedIn()) {
                    it.getOrHandleError(requireContext())
                } else {
                    null
                }
                infoBinding.refresh(info)
            }

        }
        return infoBinding.root
    }

    private fun UserInfoLayoutBinding.refresh(info: User?) {
        if (info == null) {
            userId.visibility = View.GONE
            coinInfo.visibility = View.GONE
            username.text = getString(R.string.username_un_logged_in_label)
        } else {
            userId.visibility = View.VISIBLE
            coinInfo.visibility = View.VISIBLE
            username.text = Html.fromHtml(
                getString(
                    R.string.username_label,
                    info.userInfo.username
                )
            )
            userId.text = getString(
                R.string.id_label,
                info.userInfo.id.toString()
            )
            coin.text = getString(
                R.string.coin_label,
                info.coinInfo.coinCount.toString()
            )
            level.text =
                getString(
                    R.string.level_label,
                    info.coinInfo.level.toString()
                )
            rank.text =
                getString(
                    R.string.rank_label,
                    info.coinInfo.rank.toString()
                )
        }
    }
}