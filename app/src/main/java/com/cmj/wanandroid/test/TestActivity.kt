package com.cmj.wanandroid.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.R.id
import com.cmj.wanandroid.R.layout
import com.cmj.wanandroid.base.log.LogMan
import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.api.ContentApi
import com.cmj.wanandroid.network.api.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    val userApi by lazy {
        NetworkEngine.createApi(UserApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.test_main)
        findViewById<Button>(id.register).setOnClickListener { testRegisterApi() }
        findViewById<Button>(id.login).setOnClickListener { testLoginApi() }
        findViewById<Button>(id.logout).setOnClickListener { testLogoutApi() }
        findViewById<Button>(id.home).setOnClickListener { testContentApi() }
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
                api.register(USER_NAME, USER_PASSWORD, USER_PASSWORD).awaitResponse()
                    .also { LogMan.i(TAG, "testRegisterApi: ${it.body()}") }
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

    private fun testContentApi() {
        val api = NetworkEngine.createApi(ContentApi::class.java)
        lifecycleScope.launch {
            api.articleList(0, 5).await().getOrThrow().also { LogMan.d("MCJ", "articleList: $it") }
            api.articleTop().await().getOrThrow().also { LogMan.d("MCJ", "articleTop: $it") }
            api.navi().await().getOrThrow().also { LogMan.d("MCJ", "navi: $it") }
        }
    }
}