package com.sctdroid.pullToRefresh.swipeable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;

import com.sctdroid.pullToRefresh.HeaderListView;

import java.util.List;

/**
 * 创建人： limeng
 * 日期： 10/19/15
 */

/**
 * This view is a list view that each item supports left swipe gesture.
 * When swiping, item can't be clicked and list view can't be scrolled.
 */
public class SwipeListView extends HeaderListView {

//    private boolean mSwipeEnabled;

    private static int DEL_ANIMATION_DURATION = 200;

    private static final int GESTURE_NONE = -1;
    private static final int GESTURE_DOWN = 0;
    private static final int GESTURE_TAP = 1;
    private static final int GESTURE_SCROLL_ITEM = 2;
    private static final int GESTURE_SCROLL_LIST = 3;
    private static final int GESTURE_LONGPRESS = 4;
    private static final int GESTURE_FLING_HORIZONTAL = 5;
    private static final int GESTURE_FLING_VERTICAL = 6;
    private int mGesture = GESTURE_NONE;
    private GestureDetector mGestureDetector;

    /**
     * Flag used to indicating whether any item in list is scrolling.
     */
    private boolean mGestureScrollingChild;

    /**
     * Flag used to indicate whether SwipeListAdapter has been set.
     */
    private boolean mHasSwipAdapter = false;

    /**
     * Flag used to indicating whether touch event is on an open item.
     */
    private boolean mGestureOnOpenedChild;

    /**
     * Flag used to indicating whether to interrupt the touch event.
     */
    private boolean mInterruptionGesture;

    /**
     * Child item which has been swiped open.
     */
    private SwipeListItemView mOpenedChild;
    private Rect mRect = new Rect();

    private SwipeListItemView mTouchView;

    private OnMenuItemClickListener mMenuItemClickListener;
    private SwipeMenuCreator mMenuCreator;

    // for menu background
    private Drawable mBgDrawable; // default is black
    // for menu container size
    private int mMenuContainerHeight = -1;
    private int mMenuContainerWidth = -1;

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setChoiceMode(CHOICE_MODE_SINGLE);
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                mGesture = GESTURE_DOWN;
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mGesture = GESTURE_TAP;
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mGesture != GESTURE_SCROLL_ITEM && mGesture != GESTURE_SCROLL_LIST && distanceX < 0) {
                    if (isItemScroll(-distanceX, -distanceY)) {
                        mGesture = GESTURE_SCROLL_ITEM;
                    } else {
                        mGesture = GESTURE_SCROLL_LIST;
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                mGesture = GESTURE_LONGPRESS;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isItemScroll(velocityX, velocityY)) {
                    mGesture = GESTURE_FLING_HORIZONTAL;
                } else {
                    mGesture = GESTURE_FLING_VERTICAL;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e("test: ", "---List---onInterceptTouchEvent--"+ev.getAction());
        mGestureDetector.onTouchEvent(ev);

        if (((SwipeListAdapter) getAdapter()).isAdapterAnimating()) {
            return false;
        }
        mInterruptionGesture = false;

        mOpenedChild = ((SwipeListAdapter) getAdapter()).getOpenedChild();
        if (mOpenedChild != null && !mOpenedChild.isClosed()) {
            mInterruptionGesture = true;
        }

        if (mGesture == GESTURE_DOWN) {
            mGestureOnOpenedChild = false;
            mGestureScrollingChild = false;
            if (mInterruptionGesture) {
                // click on open item
                mOpenedChild.getHitRect(mRect);
                if (mRect.contains((int) ev.getX(), (int) ev.getY())) {
                    mGestureOnOpenedChild = true;
                    return false;
                }
                mOpenedChild.closeAutonomously();
                if (mTouchView != null) {
                    mTouchView = null;
                }
                return true;
            }
        }
        if (mGestureOnOpenedChild || mGestureScrollingChild) {
            return false;
        } else if (mGesture == GESTURE_SCROLL_ITEM || mGesture == GESTURE_FLING_HORIZONTAL) {
            mGestureScrollingChild = true;
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    boolean swipeFlag = false;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.e("test: ", "---List---onTouchEvent--"+ev.getAction());
        mGestureDetector.onTouchEvent(ev);
//        Log.e("123: ", "List---mGesture--" + mGesture);
//        Log.e("123: ", "List---swipeFlag--"+swipeFlag);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                // get touch view
                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
                if (view instanceof SwipeListItemView) {
                    mTouchView = (SwipeListItemView) view;
                } else {
                    mTouchView = null;
                }
                break;
        }

        if (mGesture == GESTURE_SCROLL_ITEM || mGesture == GESTURE_FLING_HORIZONTAL || swipeFlag) {
            if (mTouchView != null) {
                mTouchView.onTouchEvent(ev);
                int originalEvent = ev.getAction();
                if (!swipeFlag) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    swipeFlag = true;
                }

                switch (originalEvent) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeFlag = false;
                        break;
                }
                return true;
            }
        }

        return mInterruptionGesture || super.onTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        SwipeListAdapter swipeListAdapter = new SwipeListAdapter(getContext(), adapter) {
            @Override
            public List<View> createMenuViews() {
                return mMenuCreator != null ? mMenuCreator.create() : super.createMenuViews();
            }

            @Override
            public void onMenuViewClick(SwipeMenuView view, List<View> menuViews, int index) {
                if (mMenuItemClickListener != null) {
                    mMenuItemClickListener.onMenuItemClick(view.getPosition(), menuViews, index);
                }
            }
        };
        swipeListAdapter.setBgDrawable(mBgDrawable == null ?
                new ColorDrawable(getResources().getColor(android.R.color.black)) : mBgDrawable);
        // for menu container height and width
        if (mMenuContainerHeight >= 0) {
            swipeListAdapter.setMenuContainerHeight(mMenuContainerHeight);
        }
        if (mMenuContainerWidth >= 0) {
            swipeListAdapter.setMenuContainerWidth(mMenuContainerWidth);
        }

        super.setAdapter(swipeListAdapter);
        mHasSwipAdapter = true;
    }

    @Override
    public ListAdapter getAdapter() {

        return mHasSwipAdapter ? ((HeaderViewListAdapter)super.getAdapter()).getWrappedAdapter() : super.getAdapter();
    }

    static boolean isItemScroll(float x, float y) {
        // change to Cartesian coordinate system
        final float cartesY = -y;
        final float cartesX = x;
        if (cartesX < 0) {
            return false;
        }
        final float ratio = cartesY / cartesX;
        return (ratio < 0.8) && (ratio > -0.8);
    }

    public boolean ifSwipingOrOpen() {
        return (mTouchView!=null && !mTouchView.isClosed()) || (mOpenedChild != null && !mOpenedChild.isClosed());
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int position, List<View> menuViews, int index);
    }

    public void setMenuItemClickListener(OnMenuItemClickListener listener) {
        mMenuItemClickListener = listener;
    }

    public void setSwipeMenuCreator(SwipeMenuCreator creator) {
        mMenuCreator = creator;
    }

    public void setMenuBackground(Drawable drawable) {
        mBgDrawable = drawable;
    }

    public void setMenuContainerHeight(int value) {
        mMenuContainerHeight = value;
    }
    public void setMenuContainerWidth(int value) {
        mMenuContainerWidth = value;
    }

    /**
     * Animation support.
     */
    public void playDeleteAnimation(final Animator.AnimatorListener listener) {
        if (mTouchView == null) {
            return;
        }

        final int scrollFrom =  mTouchView.getLeft();
        final int scrollTo =  -mTouchView.getRight();
        ValueAnimator deleteAnimator = ValueAnimator.ofInt(scrollFrom, scrollTo);
        deleteAnimator.setInterpolator(new DecelerateInterpolator(1.5F));
        deleteAnimator.setDuration(DEL_ANIMATION_DURATION);
        deleteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                mTouchView.superScrollTo(curValue, 0);
            }
        });
        deleteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ((SwipeListAdapter)getAdapter()).setAdapterAnimating(true);
                if (listener != null) {
                    listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final int height = mTouchView.getHeight();
                ValueAnimator endAnimator = ValueAnimator.ofInt(height, 1);
                endAnimator.setInterpolator(new DecelerateInterpolator(1.5F));
                endAnimator.setDuration(DEL_ANIMATION_DURATION);
                endAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int curValue = (int) animation.getAnimatedValue();
                        if (mTouchView != null) {
                            mTouchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, curValue));
                        }
                    }
                });
                endAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (listener != null) {
                            listener.onAnimationEnd(animation);
                        }
                        ((SwipeListAdapter)getAdapter()).setAdapterAnimating(false);
                        // for not reusing this view in list
                        ((SwipeListAdapter) getAdapter()).setOpenedChild(null);
                        if (mTouchView != null) {
                            mTouchView.setTag(true);
                        }
                        mTouchView = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                endAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        deleteAnimator.start();
    }
}