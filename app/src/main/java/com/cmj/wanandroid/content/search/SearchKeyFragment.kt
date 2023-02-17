package com.cmj.wanandroid.content.search

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.addRepeatingJob
import com.cmj.wanandroid.R
import com.cmj.wanandroid.lib.base.BaseFragment
import com.cmj.wanandroid.databinding.FragmentSearchKeyBinding
import com.cmj.wanandroid.lib.base.bean.Hotkey
import com.cmj.wanandroid.lib.base.kt.getOrHandleError
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.flow.collect

class SearchKeyFragment: BaseFragment<ViewModel, SearchViewModel, FragmentSearchKeyBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addRepeatingJob(Lifecycle.State.STARTED) {
            activityViewModel.hotKeyFlow.collect {
                val hotkeys = it.getOrHandleError(requireContext()) ?: return@collect
                binding.hotkeyFlex.removeAllViews()
                hotkeys.forEach { hotkey ->
                    binding.hotkeyFlex.addLabel(hotkey).setOnClickListener {
                        activityViewModel.queryKey(hotkey.name)
                    }
                }
            }
        }
    }

    private fun FlexboxLayout.addLabel(hotkey: Hotkey): View {
        val view = TextView(context, null, 0, R.style.SearchPrimaryLabelStyle).apply {
            text = hotkey.name
            isClickable = true
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.main_text_size))
        }
        addView(view)
        return view
    }
}