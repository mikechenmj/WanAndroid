package com.cmj.wanandroid.user.message

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.FragmentStubActivity
import com.cmj.wanandroid.base.page.AbsPagingFragment
import com.cmj.wanandroid.content.web.WebActivity
import com.cmj.wanandroid.network.bean.Message
import kotlinx.coroutines.flow.Flow

class ReadMessageFragment: AbsPagingFragment<ViewModel, MessageViewModel, Message>()  {

    override fun autoSubmitData(): Boolean {
        return true
    }

    override fun getPageFlow(): Flow<PagingData<Message>> {
        return activityViewModel.messageReadListFlow()
    }

    override fun initAdapter(): PagingDataAdapter<Message, out RecyclerView.ViewHolder> {
        return MessageListAdapter(requireContext()) {
            WebActivity.start(requireContext(), it.fullLink)
        }
    }
}