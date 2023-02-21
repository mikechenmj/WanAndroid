package com.cmj.wanandroid.common

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object AppToast {

    private var current: WeakReference<Toast>? = null

    fun toast(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
        enqueue(Toast.makeText(context, text, duration))
    }

    fun toast(context: Context, @StringRes text: Int, duration: Int = Toast.LENGTH_SHORT) {
        enqueue(Toast.makeText(context, text, duration))
    }

    private fun enqueue(toast: Toast) {
        GlobalScope.launch(Dispatchers.Main) {
            current?.get()?.cancel()
            current = WeakReference(toast)
            toast.show()
        }
    }

}