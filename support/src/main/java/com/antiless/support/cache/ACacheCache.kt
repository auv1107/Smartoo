package com.antiless.support.cache

import android.text.TextUtils
import android.util.Base64
import com.google.gson.Gson
import java.lang.Exception

abstract class ACacheCache<T>(private val aCache: ACache?) : CacheCache<T>() {

    override fun cache(t: T?) {
        if (t != null) {
            val encoded = Base64.encodeToString(t.toJson().toByteArray(), Base64.DEFAULT)
            aCache?.put(getTag(), encoded, getCacheTime())
        }
    }

    override fun readCache(): T? {
        val cached = aCache?.getAsString(getTag())
        var result: T? = null
        if (!TextUtils.isEmpty(cached)) {
            val decoded = String(Base64.decode(cached, Base64.DEFAULT))
            result = parseJson(decoded, getObjectClass())
        }
        return result
    }

    abstract fun getObjectClass(): Class<T>
    abstract fun getTag(): String
    open fun getCacheTime(): Int {
        return ACache.TIME_HOUR * 3
    }

    private fun <T> parseJson(json: String, clz: Class<T>): T? {
        if (TextUtils.isEmpty(json)) return null
        return try {
            Gson().fromJson(json, clz)
        } catch (e: Exception) {
            null
        }
    }

}

fun kotlin.Any.toJson(): String {
    return Gson().toJson(this)
}