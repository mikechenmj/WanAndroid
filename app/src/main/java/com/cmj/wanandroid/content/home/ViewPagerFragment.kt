package com.cmj.wanandroid.content.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmj.wanandroid.R
import com.cmj.wanandroid.content.home.ViewPagerFragment.Adapter.Holder

class ViewPagerFragment(val color: Int) : Fragment() {

    companion object {
        private val TAG = ViewPagerFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "$this onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_pager, container, false).apply {
            val recycle = findViewById<RecyclerView>(R.id.recycler)
            recycle.layoutManager = LinearLayoutManager(requireContext())
            recycle.adapter = Adapter()
            setBackgroundColor(color)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "$this onDestroy")
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {

            fun bind() {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(AppCompatImageView(requireContext()).apply {
                layoutParams = RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, 300)
                setImageResource(R.mipmap.ic_launcher)
            })
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
        }

        override fun getItemCount(): Int {
            return 20
        }
    }

}