package com.sctdroid.autosigner.presentation.ui.fragments;

import android.content.Context;

import com.sctdroid.autosigner.domain.repository.ListRepository;
import com.sctdroid.autosigner.domain.repository.PaginationRepository;
import com.sctdroid.autosigner.domain.repository.impl.FriendListRepository;
import com.sctdroid.autosigner.domain.repository.impl.FriendPaginationRepository;
import com.sctdroid.autosigner.fragments.SimpleListFragment;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by lixindong on 6/24/16.
 */
public class FriendListFragment extends SimpleListFragment {
    @Override
    protected PaginationRepository buildRepository(Context context, String appKey, Oauth2AccessToken accessToken) {
        return new FriendPaginationRepository(context, appKey, accessToken);
    }

    @Override
    protected int getLayoutResId() {
        return super.getLayoutResId();
    }
}
