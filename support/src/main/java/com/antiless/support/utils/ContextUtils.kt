package com.antiless.support.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Created by lixindong on 2018/8/12.
 */

@SuppressLint("StaticFieldLeak")
object ContextUtils {
    private lateinit var sContext: Context

    val applicationContext: Context
        get() {
            checkNotNull(sContext) { "This class is not inited yet." }

            return sContext
        }

    fun init(context: Context) {
        if (context is Application) {
            sContext = context
        } else {
            throw ParamWrongException("Context should only be ApplicationContext")
        }
    }

    class ParamWrongException(message: String) : RuntimeException(message)
}
