package com.cmj.wanandroid.lib.base.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cmj.wanandroid.lib.base.R

class LoadingDialog : DialogFragment(R.layout.loading_dialog) {

    override fun onStart() {
        val window = dialog!!.window!!
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        super.onStart()
    }
}