package com.cmj.wanandroid.network

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor

class WanAndroidCookieJar(private val cache: CookieCache, private val persistor: CookiePersistor) : PersistentCookieJar(cache, persistor) {

    fun isLoggedIn(): Boolean {
        persistor.loadAll().forEach {
//            if (it.name)
        }
//        cache.iterator().next()
        return false
    }
}