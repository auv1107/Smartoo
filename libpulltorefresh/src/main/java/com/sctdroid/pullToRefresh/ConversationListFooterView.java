/*
 * Copyright (C) 2012 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sctdroid.pullToRefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public final class ConversationListFooterView extends RelativeLayout implements
        View.OnClickListener{

    public interface FooterViewClickListener {
        void onFooterViewLoadMoreClick();
    }

    private View mLoading;
    // private View mNetworkError;
    private View mLoadMore;
    private TextView mLoadMoreTx;
    private Uri mLoadMoreUri;
    private FooterViewClickListener mClickListener;
    // Backgrounds for different states.
    private static Drawable sNormalBackground;
    public static final int STATE_GONE = 0;
    public static final int STATE_PULL_LOAD_MORE = 1;
    public static final int STATE_RELEASE_LOAD_MORE = 2;
    public static final int STATE_LOADING = 3;
    public static final int STATE_SEARCH_SERVER = 4;
    private static final boolean DBG = false;
    private static final String TAG="ConvFooterView";
    private static long mLastToastMillis;
    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 2000; // 2 seconds

    private int mCurrentState = STATE_GONE;

    public ConversationListFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mLoading = findViewById(R.id.loading);
        mLoadMore = findViewById(R.id.load_more);
        mLoadMore.setOnClickListener(this);
        mLoadMoreTx = (TextView) findViewById(R.id.load_more_text);

    }

    public void setClickListener(FooterViewClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.load_more) {
            log("onClick R.id.load_more");
            mClickListener.onFooterViewLoadMoreClick();
            setState(STATE_LOADING);
        }
    }

    public void performLoading() {
        log("performLoading mCurrentState " + mCurrentState + " mShowFooter " + isShowFooter());
        if (mCurrentState == STATE_RELEASE_LOAD_MORE && isShowFooter()) {
            mLoadMore.performClick();
        }
    }

    public boolean isShowFooter() {
        return mCurrentState != STATE_GONE;
    }

    private Drawable getBackground(int resId) {
        return getContext().getResources().getDrawable(resId);
    }

    public void setState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_PULL_LOAD_MORE:
                    mLoadMoreTx.setText(R.string.pull_up_to_load_more);
                    mLoading.setVisibility(View.GONE);
                    mLoadMore.setVisibility(View.VISIBLE);
                    break;
                case STATE_RELEASE_LOAD_MORE:
                    mLoadMoreTx.setText(R.string.pull_up_to_load_more);
                    mLoading.setVisibility(View.GONE);
                    mLoadMore.setVisibility(View.VISIBLE);
                    break;
                case STATE_LOADING:
                    mLoading.setVisibility(View.VISIBLE);
                    mLoadMore.setVisibility(View.GONE);
                    break;
                case STATE_GONE:
                    mLoading.setVisibility(View.GONE);
                    mLoadMore.setVisibility(View.GONE);
                    break;
                case STATE_SEARCH_SERVER:
                    mLoading.setVisibility(View.GONE);
                    mLoadMore.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (getState() != STATE_LOADING);
    }

    public int getState() {
        return mCurrentState;
    }

    private void log(String str) {
        if (DBG) {
            Log.d(TAG, str);
        }
    }
}
