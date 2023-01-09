package com.cmj.wanandroid.content.home

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cmj.wanandroid.R
import com.cmj.wanandroid.content.home.ContentListAdapter.ContentAdapterHolder
import com.cmj.wanandroid.databinding.ContentItemBinding
import com.cmj.wanandroid.network.bean.Content
import kotlinx.android.synthetic.main.content_item.view.star

class ContentListAdapter constructor(
    val context: Context,
    val contentConfig: ContentConfig,
    private val onItemClick: (Content) -> Unit = {},
    private val onStarClick: (Content, View) -> Unit = {_, _ ->}
) : PagingDataAdapter<Content, ContentAdapterHolder>(object : DiffUtil.ItemCallback<Content>() {
    override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onBindViewHolder(holder: ContentAdapterHolder, position: Int) {
        getItem(position)?.also {
            holder.bind(it, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentAdapterHolder {
        val binding = ContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ContentAdapterHolder(binding)
        binding.root.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition) ?: return@setOnClickListener
            onItemClick(item)
        }
        binding.star.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition) ?: return@setOnClickListener
            if (item.collect == it.star.isSelected) onStarClick(item, it)
            it.star.isSelected = !it.star.isSelected
        }
        binding.tags.isVisible = contentConfig.tags
        binding.date.isVisible = contentConfig.date
        binding.authorOrShareUser.isVisible = contentConfig.authorOrShareUser
        binding.star.isVisible = contentConfig.star
        return holder
    }

    inner class ContentAdapterHolder constructor(private val binding: ContentItemBinding) : ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(content: Content, position: Int) {
            binding.title.text = Html.fromHtml(content.title).toString()
            if (contentConfig.star) binding.star.isSelected = content.collect
            if (contentConfig.authorOrShareUser) binding.authorOrShareUser.text = context.getString(
                R.string.author_label,
                Html.fromHtml(content.author.ifBlank { content.shareUser }).toString()
            )
            if (contentConfig.date) binding.date.text = context.getString(R.string.date_label,
                content.niceDate.ifBlank { content.niceShareDate })
            if (contentConfig.tags) handleTag(content, position)
        }

        private fun handleTag(content: Content, position: Int) {
            binding.tags.removeAllViews()
            if (content.fresh) binding.tags.addView(
                TextView(context, null, 0, R.style.ColorRedTipLabelStyle).apply { setText(R.string.fresh_label) }
            )
            if (content.top) {
                binding.tags.addView(
                    TextView(context, null, 0, R.style.ColorRedTipLabelStyle).apply {
                        setText(R.string.top_label)
                    }
                )
            }
            content.tags.forEach {
                binding.tags.addView(
                    TextView(context, null, 0, R.style.ColorPrimaryLabelStyle).apply { text = it.name }
                )
            }
        }
    }

    data class ContentConfig(
        val tags: Boolean = true,
        val authorOrShareUser: Boolean = true,
        val date: Boolean = true,
        val star: Boolean = true,
    )
}
