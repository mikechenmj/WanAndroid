package com.cmj.wanandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.network.NetworkEngine
import kotlinx.coroutines.launch

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
    }

    private fun testLoginApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            api.login(USER_NAME, USER_PASSWORD).also { Log.i("MCJ", "testLoginApi: ${it.body()}") }
        }
    }

    private fun testRegisterApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            api.register(USER_NAME, USER_PASSWORD, USER_PASSWORD).also { Log.i("MCJ", "testRegisterApi: ${it.body()}") }
        }
    }

    private fun testLogoutApi() {
        val api = NetworkEngine.userApi
        lifecycleScope.launch {
            api.logout().also { Log.i("MCJ", "testLogoutApi: ${it.body()}") }
        }
    }
}