package com.antiless.support.cache

abstract class CacheCache<T> {
    private var t: T? = null
    abstract fun fetch(): T?
    fun get(forceUpdate: Boolean): T? {
        if (t == null || forceUpdate) {
            try {
                t = readCache()
                if (t == null || forceUpdate) {
                    t = fetch()
                    if (t != null) {
                        cache(t)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return t
    }

    open fun cache(t: T?) {}
    open fun readCache(): T? {
        return null
    }

    fun get(): T? {
        return get(false)
    }
}