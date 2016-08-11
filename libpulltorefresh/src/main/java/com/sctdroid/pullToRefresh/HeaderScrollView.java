package com.sctdroid.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class HeaderScrollView extends ScrollView implements IHeaderBaseView {

    private View mHeader;
    private Animation mRefreshAnim;
    private int headContentHeight = 135;
    private LinearLayout mInnerLayout;
    private RefreshHeaderView.RefreshHeaderListener mListener;

    public HeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        mInnerLayout = new LinearLayout(getContext());
        mInnerLayout.setOrientation(LinearLayout.VERTICAL);
        mHeader = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh, null, true);
        measureView(mHeader);
        headContentHeight = mHeader.getMeasuredHeight();
        mHeader.setPadding(0, -1 * headContentHeight, 0, 0);
        addView(mHeader, -1, mHeader.getLayoutParams());
        super.addView(mInnerLayout, -1, new ViewGroup.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mRefreshAnim = AnimationUtils.loadAnimation(getContext(), R.anim.refresh_anim);
        mRefreshAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        mInnerLayout.addView(child, index, params);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            child.setLayoutParams(p);
        }

        int childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void setHeaderPadding(int top) {
        mHeader.setPadding(0, top, 0, 0);
    }

    @Override
    public int getHeaderTop() {
        return mHeader.getPaddingTop();
    }

    @Override
    public int getHeaderHeight() {
        return headContentHeight;
    }

    @Override
    public int getScrollDistance() {
        return getScrollY();
    }

    @Override
    public boolean scrollToTop() {
        return fullScroll(ScrollView.FOCUS_UP);
    }

    @Override
    public void setRefreshHeaderListener(RefreshHeaderView.RefreshHeaderListener listener) {
        mListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int headerBottom = 0;
        if (getScrollY() <= 0) {
            headerBottom = getTop() + getHeaderHeight();
        }
        if (mListener != null) {
            mListener.headerClipBottomChanged(headerBottom);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        return ret;
    }
}
