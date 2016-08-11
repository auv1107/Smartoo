package com.sctdroid.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PullToRefreshScrollView extends PullToRefreshBaseView {

    private HeaderScrollView mHeaderScrollView;
    public PullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        hideLoadMore();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!(child instanceof RefreshHeaderView) && !(child instanceof IHeaderBaseView)) {
            mHeaderScrollView.addView(child, index,params);
        } else {
            super.addView(child, index, params);
        }
    }

    @Override
    public HeaderScrollView getRefreshableView() {
        return mHeaderScrollView;
    }

    @Override
    protected boolean canPredicateWhenPullDown() {
        return mHeaderScrollView.getScrollDistance() <= 0;
    }

    @Override
    protected boolean canPredicateWhenPullUpRefresh() {
        return false;
    }


    @Override
    protected boolean canPredicateWhenPullUpLoad() {
        return false;
    }

    @Override
    protected boolean isBottom(boolean pullUpRefresh) {
        return false;
    }

    @Override
    protected boolean isTop() {
        return mHeaderScrollView.getScrollDistance() <= 0;
    }

    @Override
    protected IHeaderBaseView createRefreshableView(AttributeSet attrs, boolean pullUpRefresh) {
        mHeaderScrollView = new HeaderScrollView(getContext(), attrs);
        return mHeaderScrollView;
    }


    @Override
    protected void addMoreView(View footerView) {
        //ToDo, if need will can impl it. we do nothing now.
    }

    @Override
    protected void removeMoreView(View footerView) {
        //ToDo, if need will can impl it. we do nothing now.
    }
}
