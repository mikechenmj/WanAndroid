package com.cmj.wanandroid.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.cmj.wanandroid.R

class LoadingDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.loading_dialog_layout, container, false)
    }

    override fun onStart() {
        val window = dialog!!.window!!
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        window.attributes.also {
            it.dimAmount = 0f
            window.attributes = it
        }
        super.onStart()
    }
}