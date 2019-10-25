package com.sctdroid.autosigner

import android.app.Application
import com.antiless.support.utils.ContextUtils
import com.sctdroid.autosigner.utils.AccessTokenKeeper
import com.sctdroid.autosigner.utils.WeiboApi

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        ContextUtils.init(this)
    }
}