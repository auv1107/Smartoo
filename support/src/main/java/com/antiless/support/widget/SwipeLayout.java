package com.antiless.support.widget;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by lixindong on 5/27/17.
 */

public class SwipeLayout extends SwipeRefreshLayout implements GestureDetector.OnGestureListener{
    private GestureDetector mGestureDetector;
    private OnLoadListener mOnLoadListener;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mIsLoading && distanceY > 0 && mSwipeContent.isBottom() && mSwipeContent.canLoad()) {
            mIsLoading = true;
            if (mOnLoadListener != null) {
                mOnLoadListener.onLoad();
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnLoadListener {
        void onLoad();
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mOnLoadListener = listener;
    }

    private SwipeContent mSwipeContent;
    private boolean mIsLoading = false;

    public interface SwipeContent {
        boolean canLoad();
        boolean isBottom();
    }

    public void setSwipeContent(SwipeContent swipeContent) {
        mSwipeContent = swipeContent;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
    }

    public static class LayoutManagerSwipeContent implements SwipeContent {
        private RecyclerView.LayoutManager layoutManager;
        public LayoutManagerSwipeContent(RecyclerView.LayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public boolean canLoad() {
            return true;
        }

        @Override
        public boolean isBottom() {
            int lastPosition = 0;
            if(layoutManager instanceof GridLayoutManager){
                //通过LayoutManager找到当前显示的最后的item的position
                lastPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }else if(layoutManager instanceof LinearLayoutManager){
                lastPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }else if(layoutManager instanceof StaggeredGridLayoutManager) {
                //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastCompletelyVisibleItemPositions(lastPositions);
                lastPosition = findMax(lastPositions);
            }

            return lastPosition == layoutManager.getItemCount() - 1;
        }

        private int findMax(int[] set) {
            int max = set[0];
            for (int value : set) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    }
}
