package com.cmj.wanandroid.network

import android.annotation.SuppressLint
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.NetworkUtils.NetworkType
import com.blankj.utilcode.util.NetworkUtils.OnNetworkStatusChangedListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

// 因为用到了 com.blankj.utilcode.util.UtilsBridge，需要在 Application 类创建后，才能使用
@SuppressLint("MissingPermission")
object NetworkUtil {

    private const val TAG = "NetworkUtil"

    val networkConnectedStateFlow: Flow<Boolean> = channelFlow {
        val callback = object : OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                launch {
                    send(false)
                }
            }

            override fun onConnected(networkType: NetworkType?) {
                launch {
                    send(true)
                }
            }
        }
        addOnNetworkListener(callback)
        send(isConnected)
        awaitClose { removeNetworkListener(callback) }
    }

    val isConnected: Boolean
        get() {
            return NetworkUtils.isConnected()
        }

    val isWifiConnected: Boolean
        get() {
            return NetworkUtils.isWifiConnected()
        }

    val wifiIp: String
        get() {
            return NetworkUtils.getIpAddressByWifi()
        }

    fun addOnNetworkListener(callback: OnNetworkStatusChangedListener) {
        NetworkUtils.registerNetworkStatusChangedListener(callback)
    }

    fun removeNetworkListener(callback: OnNetworkStatusChangedListener) {
        NetworkUtils.unregisterNetworkStatusChangedListener(callback)
    }
}