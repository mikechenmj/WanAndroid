package com.cmj.wanandroid.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.content.home.ContentListAdapter
import com.cmj.wanandroid.databinding.FragmentRefreshRecyclerBinding
import com.cmj.wanandroid.kt.handleError
import com.cmj.wanandroid.kt.handleIfError
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.ui.TabMediator
import com.cmj.wanandroid.ui.getColorPrimary
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

abstract class AbsContentPagingFragment<VM : ViewModel, AVM : ContentViewModel> :
    AbsContentFragment<VM, AVM, FragmentRefreshRecyclerBinding>() {

    private var refreshByUser = false
    private lateinit var pageFlow: Flow<PagingData<Content>>
    protected lateinit var contentAdapter: ContentListAdapter
    private var submitJob : Job? = null

    abstract fun getPageFlow(): Flow<PagingData<Content>>

    open fun showTag() = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageFlow = getPageFlow()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        contentAdapter = initContentAdapter()
        binding.recycler.adapter = contentAdapter
        binding.recycler.itemAnimator = null
        binding.refresh.apply {
            setColorSchemeColors(requireActivity().getColorPrimary())
            setOnRefreshListener {
                refreshByUser = true
                viewLifecycleScope.launchWhenResumed {
                    launch {
                        contentAdapter.refresh()
                        suspendCancellableCoroutine<Unit> {
                            var listener: ((CombinedLoadStates) -> Unit)? = null
                            listener = { state ->
                                state.source.forEach { _, loadState ->
                                    if (loadState !is LoadState.NotLoading) return@forEach
                                }
                                contentAdapter.removeLoadStateListener(listener!!)
                                it.resume(Unit)
                            }
                            contentAdapter.addLoadStateListener(listener)
                        }
                    }
                    isRefreshing = false
                }
            }
        }
    }

    protected fun submitData() {
        submitJob?.cancel()
        submitJob =  viewLifecycleScope.launch {
            viewLifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pageFlow.collect {
                    contentAdapter.submitData(it)
                }
            }
        }
    }

    private fun initContentAdapter(): ContentListAdapter {
        val contentAdapter = ContentListAdapter(
            requireContext(),
            showTag(),
            {

            },
            {
                handleStar(it)
            })
        submitData()
        viewLifecycleScope.launch {
            viewLifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                handlePageState(contentAdapter)
            }
        }
        return contentAdapter
    }


    private suspend fun handlePageState(adapter: ContentListAdapter) {
        adapter.loadStateFlow.sample(1000).collectLatest {
            if (refreshByUser) return@collectLatest
            var exception =
                when {
                    it.refresh is LoadState.Error -> {
                        (it.refresh as LoadState.Error).error
                    }
                    it.append is LoadState.Error -> {
                        (it.append as LoadState.Error).error
                    }
                    else -> {
                        null
                    }
                }
            if (exception != null) {
                handleError(requireContext(), exception)
                binding.refresh.isRefreshing = true
                delay(3000)
                adapter.retry()
                return@collectLatest
            }
            if (it.refresh is LoadState.Loading) {
                binding.refresh.isRefreshing = true
                return@collectLatest
            }
            if (it.append is LoadState.Loading && binding.recycler.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                binding.refresh.isRefreshing = true
                return@collectLatest
            }
            binding.refresh.isRefreshing = false
        }
    }

    private fun handleStar(content: Content) {
        viewLifecycleScope.launch {
            val result = if (content.collect) activityViewModel.unStar(content) else activityViewModel.star(content)
            if (!result.handleIfError(requireContext())) content.collect = !content.collect
        }
    }

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
    }
}