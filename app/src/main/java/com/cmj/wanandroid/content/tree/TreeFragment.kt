package com.cmj.wanandroid.content.tree

import android.util.Log
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
import com.cmj.wanandroid.databinding.TreeCategoryFlexTagsLayoutBinding
import com.cmj.wanandroid.kt.getOrHandleError
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.network.bean.Tree
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class TreeFragment : AbsContentPagingFragment<ViewModel, TreeViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.cidArticleListFlow
    }

    override fun getCollapsingView(): View {
        return initTreeCategoryView()
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig(false)
    }


    private fun initTreeCategoryView(): View {
        val binding = TreeCategoryFlexTagsLayoutBinding.inflate(LayoutInflater.from(requireContext()), view as ViewGroup?, false)
        binding.root.visibility = View.GONE
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            activityViewModel.treeCategoryFlow.collect {
                val trees = it.getOrHandleError(requireContext()) ?: return@collect
                var selectedFirst: View? = null
                var selectedSecond: View? = null
                binding.first.removeAllViews()
                trees.forEach { tree ->
                    val firstView = binding.first.addLabel(tree)
                    if (selectedFirst == null && tree.id == activityViewModel.cidFirst) selectedFirst = firstView
                    firstView.setOnClickListener {
                        activityViewModel.cidFirst = tree.id
                        binding.first.forEach { child -> child.isSelected = false }
                        firstView.isSelected = true
                        // 处理二级分类
                        binding.second.removeAllViews()
                        selectedSecond = null
                        tree.children.forEach { secondTree ->
                            val secondView = binding.second.addLabel(secondTree)
                            if (selectedFirst != null && selectedSecond == null && secondTree.id == activityViewModel.cidSecond) {
                                selectedSecond = secondView
                            }
                            secondView.setOnClickListener {
                                activityViewModel.cidSecond = secondTree.id
                                binding.second.forEach { child -> child.isSelected = false }
                                secondView.isSelected = true
                                viewLifecycleScope.launchWhenResumed {
                                    activityViewModel.submitCid(secondTree.id).join()
                                    submitData()
                                }
                            }
                        }
                        (selectedSecond ?: binding.second.getChildAt(0)).performClick()
                    }
                }
                binding.scroller.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    binding.scroller.scrollTo(activityViewModel.firstScrollX, 0)
                }
                binding.scroller.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    activityViewModel.firstScrollX = scrollX
                }
                binding.root.visibility = View.VISIBLE
                (selectedFirst ?: binding.first.getChildAt(0)).performClick()
            }
        }
        return binding.root
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