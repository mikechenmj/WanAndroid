package com.cmj.wanandroid.user.message

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.lib.base.page.AbsPagingFragment
import com.cmj.wanandroid.content.web.WebActivity
import com.cmj.wanandroid.lib.base.bean.Message
import kotlinx.coroutines.flow.Flow

class UnReadMessageFragment: AbsPagingFragment<ViewModel, MessageViewModel, Message>()  {

    override fun autoSubmitData(): Boolean {
        return true
    }

    override fun getPageFlow(): Flow<PagingData<Message>> {
        return activityViewModel.messageUnReadListFlow()
    }

    override fun initAdapter(): PagingDataAdapter<Message, out RecyclerView.ViewHolder> {
        return MessageListAdapter(requireContext()) {
            WebActivity.start(requireContext(), it.fullLink)
        }
    }
}