package com.cmj.wanandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.network.NetworkEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class TestActivity : AppCompatActivity() {

    companion object {
        private const val USER_NAME = "chenminjianTestName"
        private const val USER_PASSWORD = "chenminjianTestPassword"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_main)
        findViewById<Button>(R.id.register).setOnClickListener { testRegisterApi() }
        findViewById<Button>(R.id.login).setOnClickListener { testLoginApi() }
        findViewById<Button>(R.id.logout).setOnClickListener { testLogoutApi() }
        findViewById<Button>(R.id.test).setOnClickListener {
            val request = Request.Builder()
//        .cacheControl(CacheControl.FORCE_NETWORK)
                .url("https://www.wanandroid.com/user/logout/json").build()
            val call = NetworkEngine.okhttp.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("MCJ", "onFailure e: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("MCJ", "onResponse response: ${response.body?.string()}")
                }
            })
        }
    }

    private fun testLoginApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    api.loginTest(USER_NAME, USER_PASSWORD).also {
                        Log.i("MCJ", "it: $it")
                    }
//                    api.loginTest(USER_NAME, USER_PASSWORD).also {
//                        val a = it.execute().body()!!.getOrNull()
//                        Log.i("MCJ", "a: $a")
//                    }
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