package com.cmj.wanandroid.feature.mine.message

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.lib.base.page.AbsPagingFragment
import com.cmj.wanandroid.lib.base.web.WebActivity
import com.cmj.wanandroid.data.user.bean.Message
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