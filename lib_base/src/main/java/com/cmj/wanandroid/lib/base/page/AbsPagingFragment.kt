package com.cmj.wanandroid.lib.base.page

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.addRepeatingJob
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.lib.base.AbsDecorFragment
import com.cmj.wanandroid.lib.base.databinding.FragmentRefreshRecyclerBinding
import com.cmj.wanandroid.lib.base.kt.handleError
import com.cmj.wanandroid.lib.base.ui.TabMediator
import com.cmj.wanandroid.lib.base.ui.getColorPrimary
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlin.coroutines.resume

abstract class AbsPagingFragment<VM : ViewModel, AVM : ViewModel, T : Any> :
    AbsDecorFragment<VM, AVM, FragmentRefreshRecyclerBinding>() {

    private var refreshByUser = false
    private lateinit var pageFlow: Flow<PagingData<T>>
    protected lateinit var pagingAdapter: PagingDataAdapter<T, out RecyclerView.ViewHolder>
    private var submitJob: Job? = null

    abstract fun getPageFlow(): Flow<PagingData<T>>?

    protected abstract fun initAdapter(): PagingDataAdapter<T, out RecyclerView.ViewHolder>

    protected open fun autoSubmitData() = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageFlow = getPageFlow() ?: return
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        pagingAdapter = initAdapter()
        viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
            handlePageState(pagingAdapter)
        }
        if (autoSubmitData()) {
            submitData()

        }
        binding.recycler.adapter = pagingAdapter
        binding.recycler.itemAnimator = null
        binding.refresh.apply {
            setColorSchemeColors(requireActivity().getColorPrimary())
            setOnRefreshListener {
                refreshByUser = true
                viewLifecycleScope.launchWhenResumed {
                    launch {
                        pagingAdapter.refresh()
                        suspendCancellableCoroutine<Unit> {
                            var listener: ((CombinedLoadStates) -> Unit)? = null
                            listener = { state ->
                                state.source.forEach { _, loadState ->
                                    if (loadState !is LoadState.NotLoading) return@forEach
                                }
                                pagingAdapter.removeLoadStateListener(listener!!)
                                it.resume(Unit)
                            }
                            pagingAdapter.addLoadStateListener(listener)
                        }
                    }
                    isRefreshing = false
                }
            }
        }
    }

    private fun checkIsEmpty() {
        val empty = pagingAdapter.itemCount < 1
        binding.empty.isVisible = empty
        binding.recycler.visibility = if (empty) View.INVISIBLE else View.VISIBLE
    }

    protected fun submitData() {
        viewLifecycleScope.launchWhenStarted { //让 submitData 时机
            submitJob?.cancel()
            submitJob = viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
                pageFlow.collect {
                    pagingAdapter.submitData(it)
                }
            }

        }
    }

    private suspend fun handlePageState(adapter: PagingDataAdapter<T, out RecyclerView.ViewHolder>) {
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

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        return null
    }

    override fun getCollapsingView(): View? {
        return null
    }
}