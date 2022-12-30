package com.cmj.wanandroid.content.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.addRepeatingJob
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.databinding.ActivitySearchBinding
import com.cmj.wanandroid.kt.findNavigationById
import com.cmj.wanandroid.kt.getOrHandleError
import com.cmj.wanandroid.kt.nullOrValid
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    companion object {
        const val EXTRA_SEARCH_HOTKEY = "extra_search_hotkey"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavigationById(R.id.navigationContent)?.navController
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            binding.search.isEnabled = destination.id != R.id.searchResultFragment
        }

        addRepeatingJob(Lifecycle.State.STARTED) {
            viewModel.hotKeyFlow.collect {
                val hotkeys = it.getOrHandleError(this@SearchActivity) ?: return@collect
                binding.search.hint = intent.getStringExtra(EXTRA_SEARCH_HOTKEY).nullOrValid() ?: hotkeys.random().name
            }
        }

        addRepeatingJob(Lifecycle.State.STARTED) {
            viewModel.queryKeyFlow.collect {
                binding.search.setText(it)
                navController?.navigate(R.id.action_searchKeyFragment_to_searchResultFragment)
            }
        }

        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = binding.search.text.toString().nullOrValid() ?: binding.search.hint.toString().nullOrValid()
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