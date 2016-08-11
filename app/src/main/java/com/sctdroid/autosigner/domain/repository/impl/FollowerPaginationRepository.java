package com.sctdroid.autosigner.domain.repository.impl;

import android.content.Context;
import android.os.Bundle;

import com.sctdroid.autosigner.domain.repository.Base.BaseListRepository;
import com.sctdroid.autosigner.domain.repository.PaginationRepository;
import com.sctdroid.autosigner.models.FirendList;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;

import org.json.JSONException;

/**
 * Created by lixindong on 6/28/16.
 */
public class FollowerPaginationRepository extends BaseListRepository implements PaginationRepository {
    private static final String TAG = FollowerListRepository.class.getSimpleName();
    FriendshipsAPI mFriendshipsAPI;
    public FollowerPaginationRepository(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
        mFriendshipsAPI = new FriendshipsAPI(context, appKey, accessToken);
    }

    @Override
    public void get(Bundle bundle, final OnGetListener listener) {
        int cursor = bundle.getInt("cursor");
        int count = bundle.getInt("count");
        mFriendshipsAPI.followers(Long.valueOf(mAccessToken.getUid()), count, cursor, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
//                listener.onGet(s);
                FirendList list = FirendList.parse(s);
                if (list != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("previous_cursor", list.previous_cursor);
                    bundle.putInt("next_cursor", list.next_cursor);
                    bundle.putInt("total_number", list.total_number);
                    listener.onGet(list.models, bundle);
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
