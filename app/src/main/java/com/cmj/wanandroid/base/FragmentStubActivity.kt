package com.cmj.wanandroid.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.R
import com.cmj.wanandroid.content.home.AskFragment
import com.cmj.wanandroid.databinding.ActivityFragmentStubBinding
import kotlinx.android.synthetic.main.content_item.view.*

class FragmentStubActivity : BaseActivity<ViewModel, ActivityFragmentStubBinding>() {

    companion object {

        private const val FRAGMENT_CLASS = "fragment_class"
        private const val FRAGMENT_ARG = "fragment_arg"
        private const val ACTIVITY_TITLE = "fragment_title"

        fun start(
            context: Context,
            fragmentClass: Class<out Fragment>,
            title: String,
            bundle: Bundle? = null
        ) {
            val intent = Intent(context, FragmentStubActivity::class.java)
            intent.putExtra(FRAGMENT_CLASS, fragmentClass)
            intent.putExtra(ACTIVITY_TITLE, title)
            bundle?.let { intent.putExtra(FRAGMENT_ARG, it) }
            context.startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var fragment = supportFragmentManager.findFragmentById(R.id.container)
        binding.toolbar.title = intent.getStringExtra(ACTIVITY_TITLE)

        if (fragment == null) {
            val fragmentClass = intent.getSerializableExtra(FRAGMENT_CLASS)
                    as? Class<Fragment> ?: return
            val arg = intent.getBundleExtra(FRAGMENT_ARG)
            fragment = fragmentClass.newInstance().apply {
                arguments = arg
            }
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }
}