package com.sctdroid.autosigner.domain.repository.impl;

import android.content.Context;

import com.sctdroid.autosigner.domain.repository.ListRepository;
import com.sctdroid.autosigner.models.FirendList;
import com.sctdroid.autosigner.domain.repository.Base.BaseListRepository;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;

import org.json.JSONException;

/**
 * Created by lixindong on 6/22/16.
 */
public class FollowerListRepository extends BaseListRepository implements ListRepository {
    private static final String TAG = FollowerListRepository.class.getSimpleName();
    FriendshipsAPI mFriendshipsAPI;
    public FollowerListRepository(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
        mFriendshipsAPI = new FriendshipsAPI(context, appKey, accessToken);
    }

    @Override
    public void get(int page, int pageSize, final OnGetListener listener) {
        mFriendshipsAPI.followers(Long.valueOf(mAccessToken.getUid()), pageSize, page, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
//                listener.onGet(s);
                FirendList list = FirendList.parse(s);
                if (list != null) {
                    listener.onGet(list.models, list.previous_cursor, list.next_cursor, list.total_number);
                } else {
                    listener.onException(new JSONException("parse Exception"));
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onException(e);
            }
        });
    }
}
