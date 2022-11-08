package com.cmj.wanandroid.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.R.id
import com.cmj.wanandroid.R.layout
import com.cmj.wanandroid.base.log.LogMan
import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.bean.User
import com.cmj.wanandroid.network.api.UserApi
import com.cmj.wanandroid.network.bean.WAndroidResponse
import com.cmj.wanandroid.network.kt.flow
import com.cmj.wanandroid.network.kt.responseFlow
import com.cmj.wanandroid.network.kt.resultCall
import com.cmj.wanandroid.network.kt.resultWABodyCall
import com.cmj.wanandroid.network.kt.safeWACall
import com.cmj.wanandroid.network.kt.suspendAwait
import com.cmj.wanandroid.network.kt.suspendAwaitResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.await
import retrofit2.awaitResponse
import kotlin.Exception

class TestActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MCJ"
        private const val USER_NAME = "chenminjianTestName"
        private const val USER_PASSWORD = "chenminjianTestPassword"
    }

    val testApi by lazy {
        NetworkEngine.createApi(TestApi::class.java)
    }

    val userApi by lazy {
        NetworkEngine.createApi(UserApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.test_main)
        findViewById<Button>(id.register).setOnClickListener { testRegisterApi() }
        findViewById<Button>(id.login).setOnClickListener { testLoginApi() }
        findViewById<Button>(id.logout).setOnClickListener { testLogoutApi() }
        findViewById<Button>(id.test1).setOnClickListener {
            // 需要在 NetworkEngine 中添加对应的CallAdapterFactory，否则无法捕获网络报错或者转换失败
            lifecycleScope.launch {
                val api = testApi
                testWA(api)
                testWAResult(api)
                testWAResultBody(api)
                testWAFlow(api)
                testWALiveData(api)
                testCallAwait(api)
                testCallFlow(api)
            }
        }
        findViewById<Button>(id.test2).setOnClickListener {
            lifecycleScope.launch {
                // 测试通过操作符对 Call 进行转换
                val api = testApi
                testCallSafeResult(api)
            }
        }


    }

    private suspend fun testWA(api: TestApi) {
        withContext(Dispatchers.IO) {
            api.testWA(USER_NAME, USER_PASSWORD).execute()
        }

        suspendCancellableCoroutine<retrofit2.Response<WAndroidResponse<User>>> {
            api.testWA(USER_NAME, USER_PASSWORD).enqueue(object : retrofit2.Callback<WAndroidResponse<User>> {
                override fun onResponse(
                    call: retrofit2.Call<WAndroidResponse<User>>,
                    response: retrofit2.Response<WAndroidResponse<User>>
                ) {
                    it.resumeWith(Result.success(response))
                }

                override fun onFailure(call: retrofit2.Call<WAndroidResponse<User>>, t: Throwable) {
                    it.resumeWith(Result.failure(t))
                }

            })
        }.also { LogMan.i(TAG, "testWA enqueue: ${it.body()}") }
        api.testWAResponseSuspend(USER_NAME, USER_PASSWORD).also { LogMan.i(TAG, "testWAResponseSuspend: ${it.body()}") }
        api.testWASuspend(USER_NAME, USER_PASSWORD).also { LogMan.i(TAG, "testWASuspend: $it") }
    }

    private suspend fun testWAResult(api: TestApi) {
        withContext(Dispatchers.IO) {
            api.testWAResult(USER_NAME, USER_PASSWORD).execute().also { LogMan.i(TAG, "testWAResult execute: ${it.body()}") }
        }
        suspendCancellableCoroutine<retrofit2.Response<WAndroidResponse<User>>> {
            api.testWAResult(USER_NAME, USER_PASSWORD).enqueue(object : retrofit2.Callback<WAndroidResponse<User>> {
                override fun onResponse(
                    call: retrofit2.Call<WAndroidResponse<User>>,
                    response: retrofit2.Response<WAndroidResponse<User>>
                ) {
                    it.resumeWith(Result.success(response))
                }

                override fun onFailure(call: retrofit2.Call<WAndroidResponse<User>>, t: Throwable) {
                    it.resumeWith(Result.failure(t))
                }

            })
        }.also { LogMan.i(TAG, "testWAResult enqueue: ${it.body()}") }
        api.testWAResultResponseSuspend(USER_NAME, USER_PASSWORD).also { LogMan.i(TAG, "testWAResultResponseSuspend: ${it.body()}") }
        api.testWAResultSuspend(USER_NAME, USER_PASSWORD).also { LogMan.i(TAG, "testWAResultSuspend: $it") }
    }

    private suspend fun testWAResultBody(api: TestApi) {
        withContext(Dispatchers.IO) {
            api.testWAResultBody(USER_NAME, USER_PASSWORD).execute().also { LogMan.i(TAG, "testWAResultBody execute: ${it.body()}") }
        }
        suspendCancellableCoroutine<retrofit2.Response<WAndroidResponse<User>>> {
            api.testWAResultBody(USER_NAME, USER_PASSWORD).enqueue(object : retrofit2.Callback<WAndroidResponse<User>> {
                override fun onResponse(
                    call: retrofit2.Call<WAndroidResponse<User>>,
                    response: retrofit2.Response<WAndroidResponse<User>>
                ) {
                    it.resumeWith(Result.success(response))
                }

                override fun onFailure(call: retrofit2.Call<WAndroidResponse<User>>, t: Throwable) {
                    it.resumeWith(Result.failure(t))
                }

            })
        }.also { LogMan.i(TAG, "testWAResultBody enqueue: ${it.body()}") }
        api.testWAResultBodyResponseSuspend(USER_NAME, USER_PASSWORD).also { LogMan.i(TAG, "testWAResultBodyResponseSuspend: ${it.body()}") }
        api.testWAResultBodySuspend(USER_NAME, USER_PASSWORD).also { LogMan.i(TAG, "testWAResultBodySuspend: $it") }
    }

    private suspend fun testWAFlow(api: TestApi) {
        try {
            api.testWAResponseFlow(USER_NAME, USER_PASSWORD)
                .collect { LogMan.i(TAG, "testWAFlow: ${it.body()}") }
        } catch (e: Exception) {
            LogMan.e(TAG, "testWAResponseFlow: $e")
        }
        try {
            api.testWAFlow(USER_NAME, USER_PASSWORD).collect { LogMan.i(TAG, "testWAFlow: $it") }
        } catch (e: Exception) {
            LogMan.e(TAG, "testWAFlow: $e")
        }
    }

    private fun testWALiveData(api: TestApi) {
        api.testWALiveData(USER_NAME, USER_PASSWORD).observe(this, {
            LogMan.d(TAG, "testWALiveData: $it")
        })

        api.testWAResponseLiveData(USER_NAME, USER_PASSWORD).observe(this, {
            LogMan.d(TAG, "testWALiveData: ${it.body()}")
        })
    }

    private suspend fun testCallAwait(api: TestApi) {
        api.testWA(USER_NAME, USER_PASSWORD).suspendAwait().also { LogMan.i(TAG, "testWA await $it") }
        api.testWA(USER_NAME, USER_PASSWORD).suspendAwaitResponse().also { LogMan.i(TAG, "testWA awaitResponse ${it.body()}") }
        api.testWAResult(USER_NAME, USER_PASSWORD).suspendAwait().also { LogMan.i(TAG, "testWAResult await $it") }
        api.testWAResult(USER_NAME, USER_PASSWORD).suspendAwaitResponse().also { LogMan.i(TAG, "testWAResult awaitResponse ${it.body()}") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).suspendAwait().also { LogMan.i(TAG, "testWAResultBody await $it") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).suspendAwaitResponse()
            .also { LogMan.i(TAG, "testWAResultBody awaitResponse ${it.body()}") }
    }

    private suspend fun testCallFlow(api: TestApi) {
        api.testWA(USER_NAME, USER_PASSWORD).flow().collect { LogMan.i(TAG, "testWA asFlow $it") }
        api.testWA(USER_NAME, USER_PASSWORD).responseFlow().collect { LogMan.i(TAG, "testWA awaitResponse ${it.body()}") }
        api.testWAResult(USER_NAME, USER_PASSWORD).flow().collect { LogMan.i(TAG, "testWAResult asFlow $it") }
        api.testWAResult(USER_NAME, USER_PASSWORD).responseFlow().collect { LogMan.i(TAG, "testWAResult awaitResponse ${it.body()}") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).flow().collect { LogMan.i(TAG, "testWAResultBody asFlow $it") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).responseFlow()
            .collect { LogMan.i(TAG, "testWAResultBody awaitResponse ${it.body()}") }
    }

    private suspend fun testCallSafeResult(api: TestApi) {
        LogMan.i(TAG, "testCallSafeResult")
        withContext(Dispatchers.IO) {
            try {
                LogMan.i(
                    TAG, "safeResultCall await: ${
                        api.testWA(USER_NAME, USER_PASSWORD)
                            .resultCall().await().getOrThrow().getOrThrow()
                    }"
                )
            } catch (e: Exception) {
                LogMan.w(TAG, "safeResultCall await e: $e")
            }
            try {
                api.testWA(USER_NAME, USER_PASSWORD).resultCall().flow().collect {
                    LogMan.i(TAG, "testCallSafeResult flow: ${it.getOrThrow().getOrThrow()}")
                }
            } catch (e: Exception) {
                LogMan.w(TAG, "testCallSafeResult flow e: $e")
            }

            try {
                LogMan.i(
                    TAG, "safeResultCall await: ${
                        api.testWA(USER_NAME, USER_PASSWORD)
                            .resultWABodyCall().await().getOrThrow()
                    }"
                )
            } catch (e: Exception) {
                LogMan.w(TAG, "safeResultCall await e: $e")
            }
            try {
                api.testWA(USER_NAME, USER_PASSWORD).resultWABodyCall().flow().collect {
                    LogMan.i(TAG, "testCallSafeResult flow: ${it.getOrThrow()}")
                }
            } catch (e: Exception) {
                LogMan.w(TAG, "testCallSafeResult flow e: $e")
            }

            try {
                LogMan.i(TAG, "safeWACall await: ${api.testWA(USER_NAME, USER_PASSWORD).safeWACall().await()}")
            } catch (e: Exception) {
                LogMan.w(TAG, "safeWACall await e: $e")
            }
            try {
                api.testWA(USER_NAME, USER_PASSWORD).safeWACall().flow().collect {
                    LogMan.i(TAG, "safeWACall flow: $it")
                }
            } catch (e: Exception) {
                LogMan.w(TAG, "safeWACall flow e: $e")
            }
        }

    }

    private fun testLoginApi() {
        val api = userApi
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    api.login(USER_NAME, USER_PASSWORD).awaitResponse().also {
                        LogMan.i(TAG, "it: ${it.body()}")
                    }
                } catch (e: Exception) {
                    LogMan.e(TAG, "getOrThrow e: $e")
                    throw e
                }
            }
        }
    }

    private fun testRegisterApi() {
        val api = userApi
        lifecycleScope.launch {
            try {
                api.register(USER_NAME, USER_PASSWORD, USER_PASSWORD).awaitResponse().also { LogMan.i(TAG, "testRegisterApi: ${it.body()}") }
            } catch (e: Exception) {
                LogMan.i(TAG, "e: $e")
            }
        }
    }

    private fun testLogoutApi() {
        val api = userApi
        lifecycleScope.launch {
            api.logout().awaitResponse().also { LogMan.i(TAG, "testLogoutApi: ${it.body()}") }
        }
    }
}