package com.cmj.wanandroid.content.wxarticle

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.addRepeatingJob
import androidx.paging.PagingData
import com.cmj.wanandroid.R
import com.cmj.wanandroid.content.AbsContentPagingFragment
import com.cmj.wanandroid.content.home.ContentListAdapter
import com.cmj.wanandroid.databinding.ContentFlexTagLayoutBinding
import com.cmj.wanandroid.kt.getOrHandleError
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.network.bean.WxChapter
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WxArticleFragment : AbsContentPagingFragment<ViewModel, WxArticleViewModel>() {

    override fun getCollapsingView(): View {
        val binding = ContentFlexTagLayoutBinding.inflate(LayoutInflater.from(requireContext()), view as ViewGroup?, false)
        binding.root.visibility = View.GONE
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            activityViewModel.wxOfficialFlow.collect {
                val wxs = it.getOrHandleError(requireContext()) ?: return@collect
                var selectedView: View? = null
                binding.root.removeAllViews()
                wxs.forEach { wx ->
                    val view = binding.root.addLabel(wx)
                    if (selectedView == null && wx.id == activityViewModel.wxId) selectedView = view
                    view.setOnClickListener {
                        activityViewModel.wxId = wx.id
                        binding.root.forEach { child -> child.isSelected = false }
                        view.isSelected = true
                        viewLifecycleScope.launchWhenResumed {
                            activityViewModel.submitId(wx.id).join()
                            submitData()
                        }
                    }
                }
                binding.root.visibility = View.VISIBLE
                (selectedView ?: binding.root.getChildAt(0)).performClick()
            }
        }
        return binding.root
    }

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.wxArticleListFlow
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig(tags = false, authorOrShareUser = false)
    }

    private fun FlexboxLayout.addLabel(wx: WxChapter): View {
        val view = TextView(context, null, 0, R.style.SelectorColorPrimaryLabelStyle).apply {
            text = wx.name
            isClickable = true
            setTextSize(TypedValue.COMPLEX_UNIT_PX, requireContext().resources.getDimension(R.dimen.main_text_size))
        }
        addView(view)
        return view
    }
}