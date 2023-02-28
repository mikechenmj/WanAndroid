package com.cmj.wanandroid.feature.login.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmj.wanandroid.lib.base.BaseFragment
import com.cmj.wanandroid.common.AppToast
import com.cmj.wanandroid.data.user.UserViewModel
import com.cmj.wanandroid.feature.login.R
import com.cmj.wanandroid.feature.login.databinding.FragmentRegisterBinding
import com.cmj.wanandroid.lib.network.kt.getOrToastError

class RegisterFragment : BaseFragment<ViewModel,
        UserViewModel, FragmentRegisterBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.username.doOnTextChanged { text, start, before, count ->
            binding.clearUsername.isVisible = count > 0
        }
        binding.clearUsername.setOnClickListener {
            binding.username.text?.clear()
        }

        binding.password.doOnTextChanged { text, start, before, count ->
            binding.clearPassword.isVisible = count > 0
        }
        binding.clearPassword.setOnClickListener {
            binding.password.text?.clear()
        }

        binding.rePassword.doOnTextChanged { text, start, before, count ->
            binding.clearRePassword.isVisible = count > 0
        }
        binding.clearRePassword.setOnClickListener {
            binding.rePassword.text?.clear()
        }

        binding.register.setOnClickListener {
            register()
        }
    }

    private fun register() {
        if (binding.username.text.isNullOrBlank()) {
            AppToast.toast(requireActivity(), getString(R.string.username_empty_tip))
            return
        }
        if (binding.password.text.isNullOrBlank()) {
            AppToast.toast(requireActivity(), getString(R.string.password_empty_tip))
            return
        }
        if (binding.rePassword.text.isNullOrBlank()) {
            AppToast.toast(requireActivity(), getString(R.string.password_empty_tip))
            return
        }

        val deferred = activityViewModel.registerAsync(
            binding.username.text.toString(),
            binding.password.text.toString(),
            binding.rePassword.text.toString()
        )
        lifecycleScope.launchWhenResumed {
            showLoading()
            deferred.await().getOrToastError(requireContext()).also {
                hideLoading()
                it ?: return@launchWhenResumed
            }
            findNavController().popBackStack()
        }
    }
}