package com.cmj.wanandroid.kt

import android.util.SparseArray
import android.view.View
import com.cmj.wanandroid.base.log.LogMan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ClassCastException
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

suspend fun <T> Flow<T>.collectOldest(action: suspend (value: T) -> Unit) {
    var job: Job? = null
    val scope = newChildScope(coroutineContext)
    collect {
        if (job?.isActive == true) {
            return@collect
        }
        job = scope.launch(Dispatchers.Unconfined) {
            action(it)
        }
    }
}

suspend fun <T> SharedFlow<T>.castAndEmit(value: T): ClassCastException? {
    try {
        (this as MutableSharedFlow<T>).emit(value)
    } catch (e: ClassCastException) {
        return e
    }
    return null
}

fun <T> SharedFlow<T>.castAndTryEmit(value: T): ClassCastException? {
    try {
        (this as MutableSharedFlow<T>).tryEmit(value)
    } catch (e: ClassCastException) {
        LogMan.w("castAndTryEmit", "fail: $e")
        return e
    }
    return null
}

// 通过单线程模型确保对 flowWaiters 的线程安全
private val flowWaitersScope = MainScope()

private val flowWaiters = SparseArray<Job>()

suspend fun <T> Flow<T>.initWait(duration: Long) {
    coroutineScope {
        val key = this@initWait.hashCode()
        val job = launch {
            delay(duration)
        }
        job.invokeOnCompletion {
            flowWaitersScope.launch {
                if (flowWaiters[key] == job) {
                    flowWaiters.remove(key)
                }
            }
        }
        flowWaitersScope.launch {
            val old = flowWaiters.get(key)
            flowWaiters.put(key, job)
            old?.cancel()
        }
    }
}

suspend fun <T> Flow<T>.joinWait() {
    flowWaitersScope.launch {
        val key = this@joinWait.hashCode()
        var job = flowWaiters[key]
        while (job != null && job.isActive) {
            job.join()
            job = flowWaiters[key]
        }
    }.join()
}

const val DEFAULT_SHARED_FLOW_STOP_TIMEOUT_MILLIS = 10000L
fun <F : MutableSharedFlow<T>, T> F.doWhileSubscribed(
    scope: CoroutineScope,
    stopTimeoutMillis: Long = DEFAULT_SHARED_FLOW_STOP_TIMEOUT_MILLIS,
    action: suspend MutableSharedFlow<T>.() -> Unit
): F {
    doWhileSubscribedWithJob(scope, stopTimeoutMillis, action)
    return this
}

fun <F : MutableSharedFlow<T>, T> F.doWhileSubscribedWithJob(
    scope: CoroutineScope,
    stopTimeoutMillis: Long = 0,
    action: suspend MutableSharedFlow<T>.() -> Unit
): Job {
    var job: Job? = null
    var alreadySubscribed = false
    return scope.launch {
        subscriptionCount.collectLatest { count ->
            if (count > 0) {
                if (!alreadySubscribed) {
                    alreadySubscribed = true
                    job = launch {
                        action()
                    }
                }
            } else {
                if (stopTimeoutMillis > 0) {
                    delay(stopTimeoutMillis)
                }
                alreadySubscribed = false
                job?.cancel()
            }
        }
    }
}

fun newChildScope(parentContext: CoroutineContext): CoroutineScope {
    val newContext = run {
        val parentJob = parentContext[Job]
        val parentInterceptor = parentContext[ContinuationInterceptor] ?: EmptyCoroutineContext
        Job(parentJob) + parentInterceptor
    }
    return CoroutineScope(newContext)
}

suspend fun buffer(call: () -> Unit) {
    val flow = MutableSharedFlow<View>(0, 1)
    flow.collectLatest {
        delay(500)
        call()
    }
}

fun View.setOnClickListenerBuffer(
    scope: CoroutineScope,
    bufferDuration: Long = 500L,
    before: (View) -> Boolean = { true },
    after: (View) -> Unit = {},
    run: (View) -> Unit
) {
    val buffer = Buffer<View>(scope, bufferDuration) {
        run(it)
        after(it)
    }
    setOnClickListener {
        if (before(it)) {
            buffer.emit(it)
        }
    }
}

class Buffer<T>(
    private val scope: CoroutineScope,
    private val bufferDuration: Long = 500L,
    private val run: (T) -> Unit
) {

    private val flow = MutableSharedFlow<T>(0, 0)

    init {
        scope.launch {
            flow.collectOldest {
                run(it)
                delay(bufferDuration)
            }
        }
    }

    fun emit(value: T) {
        scope.launch { flow.emit(value) }
    }
}