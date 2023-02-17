package com.cmj.wanandroid.user.message

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cmj.wanandroid.R
import com.cmj.wanandroid.databinding.MessageItemBinding
import com.cmj.wanandroid.lib.base.bean.Message

class MessageListAdapter constructor(
    val context: Context,
    private val onItemClick: (Message) -> Unit = {},
) : PagingDataAdapter<Message, MessageListAdapter.MessageAdapterHolder>(object :
    DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onBindViewHolder(holder: MessageAdapterHolder, position: Int) {
        getItem(position)?.also {
            holder.bind(it, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.message_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapterHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = MessageAdapterHolder(binding)
        binding.root.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition) ?: return@setOnClickListener
            onItemClick(item)
        }
        return holder
    }

    inner class MessageAdapterHolder constructor(private val binding: MessageItemBinding) :
        ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(message: Message, position: Int) {
            binding.content.text = Html.fromHtml("@${message.fromUser} ${message.title}").toString()
            binding.date.text = context.getString(R.string.date_label, message.niceDate)
            if (message.tag.isNotBlank()) {
                binding.tag.isVisible = true
                binding.tag.text = message.tag
            } else {
                binding.tag.isVisible = false
            }
            binding.unread.isVisible = message.isRead == Message.UNREAD
        }
    }
}
