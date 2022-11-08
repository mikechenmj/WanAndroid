package com.cmj.wanandroid.kt

import com.cmj.wanandroid.base.log.LogMan
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private const val FATAL = "FATAL"
private const val COROUTINE_EXCEPTION = "CoroutineCatchFatal"

val coroutineExFatalHandler = CoroutineExceptionHandler { _, e ->
    LogMan.e(FATAL, "coroutineExceptionHandler exception: ${LogMan.getStackTraceString(e)}")
    throw e
}

val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
    LogMan.e(COROUTINE_EXCEPTION, "coroutineExceptionHandler exception: ${LogMan.getStackTraceString(e)}")
}


fun CoroutineScope.safeLaunch(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> Unit): Job {
    return launch(context + coroutineExceptionHandler) {
        block()
    }
}