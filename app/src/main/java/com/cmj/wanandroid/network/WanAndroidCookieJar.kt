package com.cmj.wanandroid.network

import android.util.Log
import android.webkit.CookieManager
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl

class WanAndroidCookieJar(cache: CookieCache, private val persistor: CookiePersistor) : PersistentCookieJar(cache, persistor) {

    companion object {
        private const val TOKEN_PASS_WA = "token_pass_wanandroid_com"
    }

    fun isLoggedIn(): Boolean {
        persistor.loadAll().forEach {
            if (it.name == TOKEN_PASS_WA && it.value.isNotEmpty() && it.expiresAt > System.currentTimeMillis()) {
                return true
            }
        }
        return false
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        super.saveFromResponse(url, cookies)
        val manager = CookieManager.getInstance()
        cookies.forEach {
            manager.setCookie(url.toString(), it.toString())
        }
    }
}