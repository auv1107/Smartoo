package com.sctdroid.autosigner.domain.repository.Base;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;

/**
 * Created by lixindong on 6/22/16.
 */
public class BaseListRepository {
    Context mContext;
    public BaseListRepository(Context context) {
        mContext = context;
    }
    protected Oauth2AccessToken mAccessToken;
    protected String mAppKey;
    public BaseListRepository(Context context, String appKey, Oauth2AccessToken accessToken) {
        mAccessToken = accessToken;
        mAppKey = appKey;
        mContext = context;
    }
}
