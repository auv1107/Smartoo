package com.sctdroid.autosigner.domain.model;

import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by lixindong on 6/24/16.
 */
public class UserModel implements Model {
    public static final long DEFAULT_ID = -1;
    User mUser;
    public UserModel(User user) {
        mUser = user;
    }
    @Override
    public long getItemId() {
        return mUser == null ? DEFAULT_ID : Long.valueOf(mUser.id);
    }
    public Object getObject() {
        return mUser;
    }
}
