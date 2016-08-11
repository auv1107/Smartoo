package com.sctdroid.autosigner.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.activities.TimelineActivity_;
import com.sctdroid.autosigner.presentation.ui.activities.FriendListActivity;
import com.sctdroid.autosigner.presentation.ui.activities.SimpleListActivity_;
import com.sctdroid.autosigner.utils.AccessTokenKeeper;
import com.sctdroid.autosigner.utils.Constants;
import com.sctdroid.autosigner.utils.GlideCircleTransform;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by lixindong on 1/26/16.
 */
@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;

    private Oauth2AccessToken mAccessToken;
    private UsersAPI mUserApi;

    @ViewById(R.id.login_container)
    View mLoginContainer;

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.avatar)
    ImageView avatar;

    @ViewById(R.id.introduction)
    TextView introduction;

    @ViewById(R.id.message_count)
    TextView message_count;

    @ViewById(R.id.focus_count)
    TextView focus_count;

    @ViewById(R.id.follower_count)
    TextView follower_count;

    @ViewById(R.id.cover_pic)
    ImageView cover_pic;

    @Click(R.id.logout_btn)
    void logout() {
        AccessTokenKeeper.clear(getActivity());
        mLoginContainer.setVisibility(View.VISIBLE);
    }
    @Click(R.id.login_btn)
    void login() {
        mAuthInfo = new AuthInfo(getActivity(), Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(getActivity(), mAuthInfo);
        mSsoHandler.authorizeWeb(new AuthListener());
    }
    @Click(R.id.message_container)
    void timeline() {
        TimelineActivity_.intent(getActivity()).start();
    }
    @AfterViews
    void init() {
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        if (mAccessToken.isSessionValid()) {
            mLoginContainer.setVisibility(View.GONE);
            initProfile();
        }
    }

    @UiThread
    void hideLoginContainer() {
        mLoginContainer.setVisibility(View.GONE);
    }

    @UiThread
    void initProfile() {
        mUserApi = new UsersAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        final long uid = Long.parseLong(mAccessToken.getUid());
        mUserApi.show(uid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                User user = User.parse(s);
                name.setText(user.name);
                introduction.setText(user.description);
                message_count.setText(String.valueOf(user.statuses_count));
                focus_count.setText(String.valueOf(user.friends_count));
                follower_count.setText(String.valueOf(user.followers_count));
                Glide.with(getActivity())
                        .load(user.avatar_large)
                        .transform(new GlideCircleTransform(getActivity()))
                        .into(avatar);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });
    }

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
                Toast.makeText(getActivity(), mAccessToken.toString(), Toast.LENGTH_LONG).show();
                AccessTokenKeeper.writeAccessToken(getActivity(), mAccessToken); //保存Token
                initProfile();
                hideLoginContainer();
            } else {
                // 当您注册的应用程序签名不正确时,就会收到错误Code,请确保签名正确
                String code = values.getString("code", "");
                Toast.makeText(getActivity(), "wrong code " + code, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getActivity(), "onWeiboException", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {

        }
    }

    @Click(R.id.focus_count)
    void friends() {
        startActivity(new Intent(getActivity(), FriendListActivity.class));
    }

    @Click(R.id.follower_count)
    void followers() {
        SimpleListActivity_.intent(getActivity()).start();
    }
}
