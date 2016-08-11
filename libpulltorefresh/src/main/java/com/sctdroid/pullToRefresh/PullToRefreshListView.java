package com.sctdroid.pullToRefresh;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.sctdroid.pullToRefresh.pinnedhead.PinnedHeadAdapter;
import com.sctdroid.pullToRefresh.pinnedhead.PinnedSectionListView;
import com.sctdroid.pullToRefresh.swipeable.SwipeListView;
import com.sctdroid.pullToRefresh.swipeable.SwipeMenuCreator;

import java.util.HashSet;
import java.util.Set;


public class PullToRefreshListView extends PullToRefreshBaseView {

    private HeaderListView mHeaderListView;
    private View mFooterView;

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(ListAdapter adapter) {
        // For pinned head
        // if adapter is not a pinned head adapter, then wrap it up as a pinned head adapter.
        if (adapter instanceof PinnedSectionListView.PinnedSectionListAdapter || adapter == null) {
            mHeaderListView.setAdapter(adapter);
        } else {
            mHeaderListView.setAdapter(new PinnedHeadAdapter(adapter));
        }
    }

    public void setSwipeMenuCreator(SwipeMenuCreator creator) {
        if (mHeaderListView instanceof SwipeListView) {
            ((SwipeListView) mHeaderListView).setSwipeMenuCreator(creator);
        }
    }

    public void setMenuItemClickListener(SwipeListView.OnMenuItemClickListener listener) {
        if (mHeaderListView instanceof SwipeListView) {
            ((SwipeListView) mHeaderListView).setMenuItemClickListener(listener);
        }
    }

    public void setMenuBackground(Drawable drawable) {
        if (mHeaderListView instanceof SwipeListView) {
            ((SwipeListView) mHeaderListView).setMenuBackground(drawable);
        }
    }

    public void setMenuContainerWidth(int value) {
        if (mHeaderListView instanceof SwipeListView) {
            ((SwipeListView) mHeaderListView).setMenuContainerWidth(value);
        }
    }

    public void setMenuContainerHeight(int value) {
        if (mHeaderListView instanceof SwipeListView) {
            ((SwipeListView) mHeaderListView).setMenuContainerHeight(value);
        }
    }

    public void playDeleteAnimation(final Animator.AnimatorListener listener) {
        if (mHeaderListView instanceof SwipeListView) {
            ((SwipeListView) mHeaderListView).playDeleteAnimation(listener);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public void setEmptyView(View emptyView) {
        mHeaderListView.setEmptyView(emptyView);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mHeaderListView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mHeaderListView.setOnItemLongClickListener(listener);
    }

    @Override
    protected IHeaderBaseView createRefreshableView(AttributeSet attrs, boolean enableSwipe) {
        if (enableSwipe) {
            mHeaderListView = new SwipeListView(getContext(), attrs);
        } else {
            mHeaderListView = new HeaderListView(getContext(), attrs);
        }
        mHeaderListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (listenerSet == null || listenerSet.size() == 0) return;
                for (AbsListView.OnScrollListener listener : listenerSet) {
                    listener.onScrollStateChanged(absListView, i);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (listenerSet == null || listenerSet.size() == 0) return;
                for (AbsListView.OnScrollListener listener : listenerSet) {
                    listener.onScroll(absListView, i, i1, i2);
                }
            }
        });
        return mHeaderListView;
    }

    @Override
    public HeaderListView getRefreshableView() {
        return mHeaderListView;
    }

    public SwipeListView getSwipeListView() {
        if (mHeaderListView instanceof SwipeListView) {
            return (SwipeListView) mHeaderListView;
        }
        return null;
    }

    @Override
    protected boolean canPredicateWhenPullDown() {
        return (mHeaderListView.getChildCount() == 0 || mHeaderListView.getChildAt(0).getTop() == 0);
    }

    @Override
    protected boolean canPredicateWhenPullUpRefresh() {
        // height of the list sometimes is 1 pixel less than getBottom of the last item.
        return (mHeaderListView.getChildCount() != 0 &&
                Math.abs(mHeaderListView.getChildAt(mHeaderListView.getChildCount() - 1).getBottom() - mHeaderListView.getHeight()) <= 1);
    }

    @Override
    protected boolean canPredicateWhenPullUpLoad() {
        return isBottom(false);
    }

    @Override
    protected boolean isBottom(boolean pullUpRefresh) {
        if (pullUpRefresh) {
            return mHeaderListView.getLastVisiblePosition() == mHeaderListView.getCount() - 1;
        } else {
            if (mHeaderListView.getChildCount() != 0) {
                View last = mHeaderListView.getChildAt(mHeaderListView.getChildCount() - 1);
                if (last == mFooterView) {
                    return mFooterView.getTop() <= mHeaderListView.getMeasuredHeight();
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isTop() {
        return mHeaderListView.getFirstVisiblePosition() == 0;
    }

    @Override
    protected void addMoreView(View footerView) {
        mFooterView = footerView;
        mHeaderListView.addFooterView(footerView);
        addOnScrollListener(mOnScrollListener);
    }

    @Override
    protected void removeMoreView(View footerView) {
        mHeaderListView.removeFooterView(footerView);
        mFooterView = null;
        removeOnScrollListener(mOnScrollListener);
    }

    protected Set<AbsListView.OnScrollListener> listenerSet = new HashSet<>();

    public void addOnScrollListener(AbsListView.OnScrollListener listener) {
        if (listener == null) return;
        if (!listenerSet.contains(listener)) {
            listenerSet.add(listener);
        }
    }

    public void removeOnScrollListener(AbsListView.OnScrollListener listener) {
        if (listener == null) return;
        if (listenerSet.contains(listener)) {
            listenerSet.remove(listener);
        }
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int state) {
            Log.d("pull", "state " + state);
            if (isBottom(false)) {
                if (state == 0) {
                    releaseToLoad();
                }
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    };
}
