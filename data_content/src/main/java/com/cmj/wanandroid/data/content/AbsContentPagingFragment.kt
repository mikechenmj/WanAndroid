package com.cmj.wanandroid.data.content

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.lib.base.page.AbsPagingFragment
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.lib.base.kt.handleIfError
import kotlinx.coroutines.*

abstract class AbsContentPagingFragment<VM : ViewModel, AVM : ContentViewModel> :
    AbsPagingFragment<VM, AVM, Content>() {

    protected val commonViewModel by activityViewModels<CommonViewModel>()

    open fun contentConfig() = ContentListAdapter.ContentConfig()

     override fun initAdapter(): PagingDataAdapter<Content, out RecyclerView.ViewHolder> {
        val contentAdapter = ContentListAdapter(
            requireContext(),
            contentConfig(),
            {
                ContentWebActivity.start(requireContext(), it)
            },
            { content, view ->
                handleStar(content, view)
            },
            { content, position ->
                onDeleteClick(content, position)
            })
        return contentAdapter
    }

    protected open fun onDeleteClick(content: Content, position: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.setRecycledViewPool(commonViewModel.recyclerContentItemViewPool)
    }

    private fun handleStar(content: Content, view: View) {
        viewLifecycleScope.launch {
            val result =
                if (content.collect) activityViewModel.unStar(content) else activityViewModel.star(
                    content
                )
            if (!result.handleIfError(requireContext())) {
                content.collect = !content.collect
            } else {
                view.isSelected = content.collect
            }
        }
    }

    class CommonViewModel : ViewModel() {

        val recyclerContentItemViewPool = RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(R.layout.content_item, 5)
        }

        override fun onCleared() {
            recyclerContentItemViewPool.clear()
            super.onCleared()
        }
    }
}