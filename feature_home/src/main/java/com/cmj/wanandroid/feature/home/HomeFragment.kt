package com.cmj.wanandroid.feature.home

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cmj.wanandroid.lib.base.ChildFragment
import com.cmj.wanandroid.lib.base.image.commonOption
import com.cmj.wanandroid.lib.base.AbsDecorFragment
import com.cmj.wanandroid.feature.home.HomeFragment.BannerAdapter.BannerVH
import com.cmj.wanandroid.lib.base.web.WebActivity
import com.cmj.wanandroid.data.content.bean.Banner
import com.cmj.wanandroid.feature.home.databinding.BannerLayoutBinding
import com.cmj.wanandroid.feature.home.databinding.FragmentHomeBinding
import com.cmj.wanandroid.lib.base.ui.RingPageTransformer
import com.cmj.wanandroid.lib.base.ui.TabMediator
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.filterList
import kotlin.coroutines.resume

class HomeFragment : AbsDecorFragment<HomeViewModel, ViewModel, FragmentHomeBinding>() {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
        private const val BANNER_LOOP_DELAY = 3000L
        private val CHILD_FRAGMENTS = arrayOf(
            ChildFragment(R.string.recommend_label, RecommendFragment::class.java),
            ChildFragment(R.string.ask_label, AskFragment::class.java),
            ChildFragment(R.string.share_label, ShareFragment::class.java),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = HomeAdapter()
            offscreenPageLimit = 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null //避免 leakcanary 报短暂的内存泄漏。
    }

    override fun initTabMediator(tabLayout: TabLayout?): TabMediator? {
        tabLayout ?: return null
        return TabMediator(tabLayout, binding.viewPager, true) { tab, position ->
            tab.text = getString(CHILD_FRAGMENTS[position].titleRes)
        }
    }

    override fun getCollapsingView(): View {
        return ViewPager2(requireContext()).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setPageTransformer(RingPageTransformer())
            viewLifecycleScope.launchWhenResumed {
                var job: Job? = null
                fun nextDelay(delay: Long = BANNER_LOOP_DELAY, smooth: Boolean = true) = launch {
                    delay(delay)
                    val count = adapter?.itemCount ?: return@launch
                    val next = if (currentItem < count - 1) currentItem + 1 else 0
                    setCurrentItem(next, smooth)
                }
                viewLifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_RESUME) {
                            nextDelay(0, false)
                        }
                    }
                })
                registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        if (state == SCROLL_STATE_IDLE) {
                            job = nextDelay()
                        } else {
                            job?.cancel()
                        }
                    }
                })
                viewModel.bannerFlow.collectLatest {
                    val banners = it.getOrNull()?.filterList {
                        suspendCancellableCoroutine { continuation -> //先进行预加载，加载成功的才可见。
                            Glide.with(requireContext())
                                .asBitmap()
                                .commonOption()
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .load(this.imagePath)
                                .listener(object : RequestListener<Bitmap> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Bitmap>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        continuation.resume(false)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Bitmap?,
                                        model: Any?,
                                        target: Target<Bitmap>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        continuation.resume(true)
                                        return false
                                    }
                                })
                                .preload()
                        }
                    }?.toMutableList()
                    if (banners.isNullOrEmpty()) {
                        return@collectLatest
                    }
                    banners.sort()
                    adapter = BannerAdapter(this@apply).apply {
                        setData(banners)
                    }
                    if (job?.isActive != true) {
                        job = nextDelay()
                    }
                }
            }
        }
    }

    inner class BannerAdapter(private val viewPager2: ViewPager2) :
        RecyclerView.Adapter<BannerVH>() {

        private lateinit var banners: List<Banner>

        fun setData(banners: List<Banner>) {
            this.banners = banners
            viewPager2.addOnLayoutChangeListener(object : OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                    oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    if (top - bottom != 0 && right - left != 0) {
                        val center = Int.MAX_VALUE / 2
                        viewPager2.setCurrentItem(center - center % banners.size, false)
                        viewPager2.removeOnLayoutChangeListener(this)
                    }
                }
            })
            notifyDataSetChanged()
        }

        fun getData(): List<Banner> {
            return banners
        }

        private fun getRealPosition(position: Int): Int {
            return position % getData().size
        }

        inner class BannerVH(private val binding: BannerLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int) {
                val banner = banners[position]
                binding.bannerTitle.text = banner.title
                Glide.with(requireContext())
                    .asBitmap()
                    .commonOption()
                    .load(banner.imagePath)
                    .into(binding.bannerImage)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerVH {
            val binding = BannerLayoutBinding.inflate(layoutInflater, parent, false)
            val holder = BannerVH(binding)
            binding.root.setOnClickListener {
                val position = getRealPosition(holder.layoutPosition)
                WebActivity.start(requireContext(), banners[position].url)
            }
            return holder
        }

        override fun onBindViewHolder(holder: BannerVH, position: Int) {
            holder.bind(getRealPosition(position))
        }

        override fun getItemCount(): Int {
            return Int.MAX_VALUE
        }
    }

    inner class HomeAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return CHILD_FRAGMENTS.size
        }

        override fun createFragment(position: Int): Fragment {
            return CHILD_FRAGMENTS[position].clazz.newInstance()
        }
    }
}
