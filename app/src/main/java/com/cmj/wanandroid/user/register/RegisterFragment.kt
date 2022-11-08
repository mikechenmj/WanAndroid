package com.cmj.wanandroid.user.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.BaseFragment
import com.cmj.wanandroid.databinding.FragmentLoginBinding
import com.cmj.wanandroid.databinding.FragmentRegisterBinding
import com.cmj.wanandroid.kt.getOrToastError
import com.cmj.wanandroid.main.MainActivity
import com.cmj.wanandroid.ui.AppToast
import com.cmj.wanandroid.user.UserViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.LazyThreadSafetyMode.NONE

class RegisterFragment : BaseFragment<ViewModel, UserViewModel, FragmentRegisterBinding>() {

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