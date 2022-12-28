package com.cmj.wanandroid.content.project

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
import com.cmj.wanandroid.network.bean.Tree
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProjectFragment: AbsContentPagingFragment<ViewModel, ProjectViewModel>() {

    override fun getCollapsingView(): View {
        val binding = ContentFlexTagLayoutBinding.inflate(LayoutInflater.from(requireContext()), view as ViewGroup?, false)
        binding.root.visibility = View.GONE
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            activityViewModel.projectTreeCategoryFlow.collect {
                val projectTree = it.getOrHandleError(requireContext()) ?: return@collect
                var selectedView: View? = null
                binding.root.removeAllViews()
                projectTree.forEach { tree ->
                    val view = binding.root.addLabel(tree)
                    if (selectedView == null && tree.id == activityViewModel.projectCid) selectedView = view
                    view.setOnClickListener {
                        activityViewModel.projectCid = tree.id
                        binding.root.forEach { child -> child.isSelected = false }
                        view.isSelected = true
                        viewLifecycleScope.launchWhenResumed {
                            activityViewModel.submitCid(tree.id).join()
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
        return activityViewModel.projectListFlow
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig(tags = false)
    }

    private fun FlexboxLayout.addLabel(tree: Tree): View {
        val view = TextView(context, null, 0, R.style.SelectorColorPrimaryLabelStyle).apply {
            text = tree.name
            isClickable = true
            setTextSize(TypedValue.COMPLEX_UNIT_PX, requireContext().resources.getDimension(R.dimen.main_text_size))
        }
        addView(view)
        return view
    }

}