package com.cmj.wanandroid.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.R.id
import com.cmj.wanandroid.R.layout
import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.User
import com.cmj.wanandroid.network.WAndroidResponse
import com.cmj.wanandroid.network.kt.flow
import com.cmj.wanandroid.network.kt.responseFlow
import com.cmj.wanandroid.network.kt.resultCall
import com.cmj.wanandroid.network.kt.resultWABodyCall
import com.cmj.wanandroid.network.kt.safeWACall
import com.cmj.wanandroid.network.kt.suspendAwait
import com.cmj.wanandroid.network.kt.suspendAwaitResponse
import com.cmj.wanandroid.network.test.TestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.await
import kotlin.Exception

class TestActivity : AppCompatActivity() {

    companion object {
        private const val USER_NAME = "chenminjianTestName"
        private const val USER_PASSWORD = "chenminjianTestPassword"
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
                val api = NetworkEngine.testApi
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
                val api = NetworkEngine.testApi
                testCallSafeResult(api)
            }
        }


    }

    private suspend fun testWA(api: TestApi) {
        withContext(Dispatchers.IO) {
            api.testWA(USER_NAME, USER_PASSWORD).execute().also { Log.i("MCJ", "testWA execute: ${it.body()}") }
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
        }.also { Log.i("MCJ", "testWA enqueue: ${it.body()}") }
        api.testWAResponseSuspend(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testWAResponseSuspend: ${it.body()}") }
        api.testWASuspend(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testWASuspend: $it") }
    }

    private suspend fun testWAResult(api: TestApi) {
        withContext(Dispatchers.IO) {
            api.testWAResult(USER_NAME, USER_PASSWORD).execute().also { Log.i("MCJ", "testWAResult execute: ${it.body()}") }
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
        }.also { Log.i("MCJ", "testWAResult enqueue: ${it.body()}") }
        api.testWAResultResponseSuspend(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testWAResultResponseSuspend: ${it.body()}") }
        api.testWAResultSuspend(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testWAResultSuspend: $it") }
    }

    private suspend fun testWAResultBody(api: TestApi) {
        withContext(Dispatchers.IO) {
            api.testWAResultBody(USER_NAME, USER_PASSWORD).execute().also { Log.i("MCJ", "testWAResultBody execute: ${it.body()}") }
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
        }.also { Log.i("MCJ", "testWAResultBody enqueue: ${it.body()}") }
        api.testWAResultBodyResponseSuspend(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testWAResultBodyResponseSuspend: ${it.body()}") }
        api.testWAResultBodySuspend(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testWAResultBodySuspend: $it") }
    }

    private suspend fun testWAFlow(api: TestApi) {
        try {
            api.testWAResponseFlow(USER_NAME, USER_PASSWORD)
                .collect { Log.i("MCJ", "testWAFlow: ${it.body()}") }
        } catch (e: Exception) {
            Log.e("MCJ", "testWAResponseFlow: $e")
        }
        try {
            api.testWAFlow(USER_NAME, USER_PASSWORD).collect { Log.i("MCJ", "testWAFlow: $it") }
        } catch (e: Exception) {
            Log.e("MCJ", "testWAFlow: $e")
        }
    }

    private fun testWALiveData(api: TestApi) {
        api.testWALiveData(USER_NAME, USER_PASSWORD).observe(this, {
            Log.d("MCJ", "testWALiveData: $it")
        })

        api.testWAResponseLiveData(USER_NAME, USER_PASSWORD).observe(this, {
            Log.d("MCJ", "testWALiveData: ${it.body()}")
        })
    }

    private suspend fun testCallAwait(api: TestApi) {
        api.testWA(USER_NAME, USER_PASSWORD).suspendAwait().also { Log.i("MCJ", "testWA await $it") }
        api.testWA(USER_NAME, USER_PASSWORD).suspendAwaitResponse().also { Log.i("MCJ", "testWA awaitResponse ${it.body()}") }
        api.testWAResult(USER_NAME, USER_PASSWORD).suspendAwait().also { Log.i("MCJ", "testWAResult await $it") }
        api.testWAResult(USER_NAME, USER_PASSWORD).suspendAwaitResponse().also { Log.i("MCJ", "testWAResult awaitResponse ${it.body()}") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).suspendAwait().also { Log.i("MCJ", "testWAResultBody await $it") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).suspendAwaitResponse()
            .also { Log.i("MCJ", "testWAResultBody awaitResponse ${it.body()}") }
    }

    private suspend fun testCallFlow(api: TestApi) {
        api.testWA(USER_NAME, USER_PASSWORD).flow().collect { Log.i("MCJ", "testWA asFlow $it") }
        api.testWA(USER_NAME, USER_PASSWORD).responseFlow().collect { Log.i("MCJ", "testWA awaitResponse ${it.body()}") }
        api.testWAResult(USER_NAME, USER_PASSWORD).flow().collect { Log.i("MCJ", "testWAResult asFlow $it") }
        api.testWAResult(USER_NAME, USER_PASSWORD).responseFlow().collect { Log.i("MCJ", "testWAResult awaitResponse ${it.body()}") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).flow().collect { Log.i("MCJ", "testWAResultBody asFlow $it") }
        api.testWAResultBody(USER_NAME, USER_PASSWORD).responseFlow()
            .collect { Log.i("MCJ", "testWAResultBody awaitResponse ${it.body()}") }
    }

    private suspend fun testCallSafeResult(api: TestApi) {
        Log.i("MCJ", "testCallSafeResult")
        withContext(Dispatchers.IO) {
            try {
                Log.i(
                    "MCJ", "safeResultCall await: ${
                        api.testWA(USER_NAME, USER_PASSWORD)
                            .resultCall().await().getOrThrow().getOrThrow()
                    }"
                )
            } catch (e: Exception) {
                Log.w("MCJ", "safeResultCall await e: $e")
            }
            try {
                api.testWA(USER_NAME, USER_PASSWORD).resultCall().flow().collect {
                    Log.i("MCJ", "testCallSafeResult flow: ${it.getOrThrow().getOrThrow()}")
                }
            } catch (e: Exception) {
                Log.w("MCJ", "testCallSafeResult flow e: $e")
            }

            try {
                Log.i(
                    "MCJ", "safeResultCall await: ${
                        api.testWA(USER_NAME, USER_PASSWORD)
                            .resultWABodyCall().await().getOrThrow()
                    }"
                )
            } catch (e: Exception) {
                Log.w("MCJ", "safeResultCall await e: $e")
            }
            try {
                api.testWA(USER_NAME, USER_PASSWORD).resultWABodyCall().flow().collect {
                    Log.i("MCJ", "testCallSafeResult flow: ${it.getOrThrow()}")
                }
            } catch (e: Exception) {
                Log.w("MCJ", "testCallSafeResult flow e: $e")
            }

            try {
                Log.i("MCJ", "safeWACall await: ${api.testWA(USER_NAME, USER_PASSWORD).safeWACall().await()}")
            } catch (e: Exception) {
                Log.w("MCJ", "safeWACall await e: $e")
            }
            try {
                api.testWA(USER_NAME, USER_PASSWORD).safeWACall().flow().collect {
                    Log.i("MCJ", "safeWACall flow: $it")
                }
            } catch (e: Exception) {
                Log.w("MCJ", "safeWACall flow e: $e")
            }
        }

    }

    private fun testLoginApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    api.loginSuspend(USER_NAME, USER_PASSWORD).also {
                        Log.i("MCJ", "it: ${it.body()}")
                    }
                } catch (e: Exception) {
                    Log.e("MCJ", "getOrThrow e: $e")
                    throw e
                }
            }
        }
    }

    private fun testRegisterApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            try {
                api.registerSuspend(USER_NAME, USER_PASSWORD, USER_PASSWORD).also { Log.i("MCJ", "testRegisterApi: ${it.body()}") }
            } catch (e: Exception) {
                Log.i("MCJ", "e: $e")
            }
        }
    }

    private fun testLogoutApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            api.logoutSuspend().also { Log.i("MCJ", "testLogoutApi: ${it.body()}") }
        }
    }
}