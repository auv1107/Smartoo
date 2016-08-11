package com.sctdroid.pullToRefresh;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.sctdroid.pullToRefresh.pinnedhead.PinnedSectionListView;

/**
 * Created by yangyang on 15-7-21.
 */
public class HeaderListView extends PinnedSectionListView implements AbsListView.OnScrollListener, IHeaderBaseView, IFooterBaseView {

    private View mHeader;
    private View mFooter;
    private int refreshContentHeight = 135;

    private int mScrollState = SCROLL_STATE_IDLE;

    private RefreshHeaderView.RefreshHeaderListener mListener;
    private RefreshFooterView.RefreshFooterListener mFooterListener;

    public HeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView(){
        if (getDividerHeight() != 0) {
            setOverscrollHeader(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
        mHeader = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh, null, true);
        measureView(mHeader);
        refreshContentHeight = mHeader.getMeasuredHeight();
        mHeader.setPadding(0, -1 * refreshContentHeight, 0, 0);
        mHeader.invalidate();
        this.addHeaderView(mHeader, null, false);

        // for footer
        boolean mPullUpRefreshEnabled = true;
        if (mPullUpRefreshEnabled) {
            if (getDividerHeight() != 0) {
                setOverscrollFooter(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            }
            mFooter = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh, null, true);
            measureView(mFooter);
            mFooter.setPadding(0, 0, 0, -1 * refreshContentHeight);
            mFooter.invalidate();
            this.addFooterView(mFooter, null, false);
        }

        setHeaderDividersEnabled(false);
        setFooterDividersEnabled(false);

        super.setOnScrollListener(this);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            child.setLayoutParams(p);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
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
    public int getHeaderHeight() {
        return refreshContentHeight;
    }
    @Override
    public int getHeaderTop() {
        return mHeader.getPaddingTop();
    }
    @Override
    public void setHeaderPadding(int top) {
        mHeader.setPadding(0, top, 0, 0);

        if (getChildCount() - getHeaderViewsCount() - getFooterViewsCount() == 0
                && getEmptyView() != null) {
            int topPadding = top + this.getHeaderHeight();
            if(topPadding < 0){
                topPadding = 0;
            }
            getEmptyView().setPadding(0, topPadding , 0, 0);
        }
    }
    // for footer view
    @Override
    public void setFooterPadding(int bottom) {
        mFooter.setPadding(0, 0, 0, bottom);
        if (getChildCount() - getHeaderViewsCount() - getFooterViewsCount() == 0
                && getEmptyView() != null) {
            int bottomPadding = bottom + this.getFooterHeight();
            if(bottomPadding < 0){
                bottomPadding = 0;
            }
            getEmptyView().setPadding(0, 0 , 0, bottomPadding);
        }
    }

    @Override
    public int getFooterBottom() {
        return mFooter.getPaddingBottom();
    }

    @Override
    public int getFooterHeight() {
        return refreshContentHeight;
    }

    @Override
    public boolean scrollToBottom() {
        setSelection(getCount() - 1);
        return true;
    }

    @Override
    public void setRefreshHeaderListener(RefreshHeaderView.RefreshHeaderListener l) {
        mListener = l;
    }
    @Override
    public void setRefreshFooterListener(RefreshFooterView.RefreshFooterListener listener) {
        mFooterListener = listener;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int headerBottom = 0;
        if (firstVisibleItem == 0  &&  getChildAt(firstVisibleItem) != null) {
            headerBottom = getTop() + getHeaderHeight();
        }
        if (mListener != null) {
            mListener.headerClipBottomChanged(headerBottom);
        }
    }

    @Override
    public boolean scrollToTop() {
        if (!isScrolling()) {
            setSelection(0);
        }
        return true;
    }

    @Override
    public int getScrollDistance() {
        return getScrollY();
    }

    private boolean isScrolling(){
        return mScrollState != SCROLL_STATE_IDLE;
    }

    public void removeFooter() {
        this.removeFooterView(mFooter);
    }

    public void addFooter() {
        mFooter.setPadding(0, 0, 0, -refreshContentHeight);
        this.addFooterView(mFooter);
    }
}
