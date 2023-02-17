package com.cmj.wanandroid.user.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.BaseFragment
import com.cmj.wanandroid.databinding.FragmentLoginBinding
import com.cmj.wanandroid.common.AppToast
import com.cmj.wanandroid.common.kt.setOnClickListenerBuffer
import com.cmj.wanandroid.lib.network.kt.getOrToastError
import com.cmj.wanandroid.user.UserActivity
import com.cmj.wanandroid.user.UserViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment : BaseFragment<LoginViewModel, UserViewModel, FragmentLoginBinding>() {

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
            binding.passwordCover.isVisible = count > 0
        }

        binding.clearPassword.setOnClickListener {
            binding.password.text?.clear()
        }
        viewModel.passwordCoverFlow.onEach {
            binding.passwordCover.setImageResource(if (it) R.drawable.ic_password_cover else R.drawable.ic_password_discover)
            binding.password.inputType =
                if (it)
                    EditorInfo.TYPE_TEXT_VARIATION_PASSWORD or EditorInfo.TYPE_CLASS_TEXT
                else
                    EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_CLASS_TEXT
        }.launchIn(viewLifecycleScope)
        binding.passwordCover.setOnClickListener {
            viewModel.setPasswordCover(!viewModel.passwordCoverFlow.value)
        }

        binding.login.setOnClickListener {
            login()
        }

        binding.register.setOnClickListenerBuffer(viewLifecycleScope) {
            val parent = activity as? UserActivity ?: return@setOnClickListenerBuffer
            parent.navigateToRegister()
        }
    }

    private fun login() {
        if (binding.username.text.isNullOrBlank()) {
            AppToast.toast(requireActivity(), getString(R.string.username_empty_tip))
            return
        }
        if (binding.password.text.isNullOrBlank()) {
            AppToast.toast(requireActivity(), getString(R.string.password_empty_tip))
            return
        }

        val deferred = activityViewModel.loginAsync(binding.username.text.toString(), binding.password.text.toString())
        viewLifecycleScope.launchWhenResumed {
            showLoading()
            deferred.await().getOrToastError(requireContext()).also {
                hideLoading()
                it ?: return@launchWhenResumed
            }
            requireActivity().finish()
        }
    }
}