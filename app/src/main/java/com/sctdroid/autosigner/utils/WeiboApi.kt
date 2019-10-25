package com.sctdroid.autosigner.utils

import com.antiless.support.utils.ContextUtils
import com.sina.weibo.sdk.openapi.CommentsAPI

class WeiboApi {
    companion object {
        fun commentsAPI(): CommentsAPI? {
            val accessToken = AccessTokenKeeper.readAccessToken(ContextUtils.applicationContext)
            if (accessToken.isSessionValid) {
                return CommentsAPI(ContextUtils.applicationContext, Constants.APP_KEY, accessToken)
            }
            return null
        }
    }
}