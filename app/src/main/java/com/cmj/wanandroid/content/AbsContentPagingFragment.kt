package com.cmj.wanandroid.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.addRepeatingJob
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.R
import com.cmj.wanandroid.content.web.ContentWebActivity
import com.cmj.wanandroid.content.home.ContentListAdapter
import com.cmj.wanandroid.databinding.FragmentRefreshRecyclerBinding
import com.cmj.wanandroid.kt.handleError
import com.cmj.wanandroid.kt.handleIfError
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.ui.TabMediator
import com.cmj.wanandroid.ui.getColorPrimary
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.resume

//封装了基于 ContentListAdapter 的 Paging flow 的处理。
abstract class AbsContentPagingFragment<VM : ViewModel, AVM : ContentViewModel> :
    AbsContentFragment<VM, AVM, FragmentRefreshRecyclerBinding>() {

    private var refreshByUser = false
    private lateinit var pageFlow: Flow<PagingData<Content>>
    protected lateinit var contentAdapter: ContentListAdapter
    private var submitJob: Job? = null
    protected val commonViewModel by activityViewModels<CommonViewModel>()

    abstract fun getPageFlow(): Flow<PagingData<Content>>?

    open fun contentConfig() = ContentListAdapter.ContentConfig()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageFlow = getPageFlow() ?: return
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        contentAdapter = initContentAdapter()
        submitData()
        binding.recycler.adapter = contentAdapter
        binding.recycler.setRecycledViewPool(commonViewModel.recyclerContentItemViewPool)
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
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            contentAdapter.refresh()
        }
    }

    private fun checkIsEmpty() {
        val empty = contentAdapter.itemCount < 1
        binding.empty.isVisible = empty
        binding.recycler.visibility = if (empty) View.INVISIBLE else View.VISIBLE
    }

    protected fun submitData() {
        submitJob?.cancel()
        submitJob = viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            pageFlow.collect {
                contentAdapter.submitData(it)
            }
        }
    }

    private fun initContentAdapter(): ContentListAdapter {
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
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            handlePageState(contentAdapter)
        }
        return contentAdapter
    }

    open protected fun onDeleteClick(content: Content, position: Int) {
    }

    private suspend fun handlePageState(adapter: ContentListAdapter) {
        coroutineScope {
            launch {
                adapter.onPagesUpdatedFlow
                    .conflate()
                    .collect {
                        checkIsEmpty()
                        delay(1000)
                    }
            }

            launch {
                adapter.loadStateFlow
                    .conflate() //避免频率过快地接收数据导致UI跳变
                    .collect {
                        if (refreshByUser) return@collect
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
                            return@collect
                        }
                        if (it.refresh is LoadState.Loading) {
                            binding.refresh.isRefreshing = true
                            delay(1000)
                            return@collect
                        }
                        if (it.append is LoadState.Loading && binding.recycler.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                            binding.refresh.isRefreshing = true
                            delay(1000)
                            return@collect
                        }
                        binding.refresh.isRefreshing = false
                    }
            }
        }

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

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
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