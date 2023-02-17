package com.cmj.wanandroid.content.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.cmj.wanandroid.R
import com.cmj.wanandroid.lib.base.BaseActivity
import com.cmj.wanandroid.databinding.ActivitySearchBinding
import com.cmj.wanandroid.lib.base.kt.findNavigationById
import com.cmj.wanandroid.common.kt.nullOrValid
import com.cmj.wanandroid.lib.base.kt.getOrHandleError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    companion object {
        const val EXTRA_SEARCH_HOTKEY = "extra_search_hotkey"
        const val EXTRA_SEARCH_PERFORM = "extra_search_perform"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavigationById(R.id.navigationContent)?.navController
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            binding.search.isEnabled = destination.id != R.id.searchResultFragment
        }

        val hotkey = intent.getStringExtra(EXTRA_SEARCH_HOTKEY).nullOrValid()
        if (hotkey != null) {
            if (intent.getBooleanExtra(EXTRA_SEARCH_PERFORM, false)) {
                viewModel.queryKey(hotkey)
            }
            lifecycleScope.launchWhenResumed {
                viewModel.hotKeyFlow.collect {
                    val hotkeys = it.getOrHandleError(this@SearchActivity) ?: return@collect
                    binding.search.hint = hotkeys.random().name
                }
            }
        } else {
            binding.search.hint = hotkey
        }

        lifecycleScope.launchWhenResumed {
            viewModel.queryKeyFlow.collect {
                binding.search.setText(it)
                val option = NavOptions.Builder()
                    .setPopUpTo(
                        R.id.searchKeyFragment,
                        intent.getBooleanExtra(EXTRA_SEARCH_PERFORM, false)
                    )
                    .build()
                navController?.navigate(
                    R.id.action_searchKeyFragment_to_searchResultFragment,
                    null,
                    option
                )
            }
        }

        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key =
                    binding.search.text.toString().nullOrValid() ?: binding.search.hint.toString()
                        .nullOrValid()
                key ?: return@setOnEditorActionListener false
                viewModel.queryKey(key)
            }
            false
        }

        binding.search.requestFocus()
        lifecycleScope.launchWhenResumed {
            delay(500)
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(binding.search, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}