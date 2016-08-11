package com.sctdroid.pullToRefresh.swipeable;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.OverScroller;

/**
 * 创建人： limeng
 * 日期： 10/23/15
 */

/**
 *  This view is the swipe list item view.
 *  It has two layer. Top layer is the main content for swipe list item, and the bottom layer is the menu view for each item.
 */
public class SwipeListItemView extends FrameLayout {
    private View mContentView;
    private SwipeMenuView mMenuView;
    private int mPosition;

    public static final int UNDEFINED = Integer.MIN_VALUE;

    private GestureDetector mGestureDetector;

    private int mGestureStartLeft = -1;
    private int mLeftOpenBalanceDistance;

    /**
     * Current left position of the upper view.
     */
    private int mCurrentCoverLeft = UNDEFINED;

    /**
     * Left position of the upper view when item is closed.
     */
    private int mCloseCoverLeft = UNDEFINED;

    /**
     * Left position of the upper view when item is open.
     */
    private int mLeftOpenCoverLeft;

    /**
     * Right edge of menu view.
     */
    private int mLeftDampDistance;

    /**
     * Flag that indicating whether touch event is a scroll gesture.
     */
    private boolean mIsScrollGesture = false;

    private boolean mIgnoreGesture = false;

    private MyOverScroller mScroller;

    private static final int SCROLL_DURATION = 250;
    private static final int FLING_DURATION = 200;
    private static final int REBOUND_DURATION = 150;

    /**
     * Reference for adapter.
     */
    private SwipeListAdapter mAdapter;

    /**
     * Scroller doesn't seem to turn to finished state promptly.
     * So we add some check in #isClearFinished() to get the finish state earlier than super.finish().
     */
    class MyOverScroller extends OverScroller {
        boolean mIgnoreInterrupt = false;

        public MyOverScroller(Context context) {
            super(context);
        }

        public MyOverScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public void startScroll(int startX, int startY, int dx, int dy, boolean ignoreInterrupt) {
            super.startScroll(startX, startY, dx, dy);
            mIgnoreInterrupt = ignoreInterrupt;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            startScroll(startX, startY, dx, dy, false);
        }

        public void startScroll(int startX, int startY, int dx, int dy, int duration,
                                boolean ignoreInterrupt) {
            super.startScroll(startX, startY, dx, dy, duration);
            mIgnoreInterrupt = ignoreInterrupt;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            startScroll(startX, startY, dx, dy, duration, false);
        }

        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX,
                          int minY, int maxY, boolean ignoreInterrupt) {
            super.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            mIgnoreInterrupt = ignoreInterrupt;
        }

        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX,
                          int minY, int maxY) {
            fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, false);
        }

        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX,
                          int minY, int maxY, int overX, int overY, boolean ignoreInterrupt) {
            super.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
            mIgnoreInterrupt = ignoreInterrupt;
        }

        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX,
                          int minY, int maxY, int overX, int overY) {
            fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY,
                    false);
        }

        public boolean ignoreInterrupt() {
            boolean finished = isClearFinished();
            if (finished) {
                mIgnoreInterrupt = false;
            }
            return mIgnoreInterrupt;
        }

        public boolean isClearFinished() {
            return isFinished() ||
                    isFlingFinished();
        }

        private boolean isFlingFinished() {
            return  (getCurrX() == getFinalX())
                    && (getCurrY() == getFinalY())
                    // At some point, getCurrVelocity() != 0 and the item is really closed.
                    /* && (getCurrVelocity() == 0) */;
        }

    }

    public SwipeListItemView(Context context) {
        super(context);
    }

    public SwipeListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeListItemView(View content, SwipeMenuView menu) {
        super(content.getContext());
        mContentView = content;
        mMenuView = menu;
        mMenuView.setSwipeListItemView(this);
        init();
    }

    private void init() {
        mScroller = new MyOverScroller(getContext(), new DecelerateInterpolator());
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            float mOverScrollX;

            /**
             * Used to avoid precision lost when casting float to int.
             */
            float mTotalVectorX;
            int mTotalVectorXInt;

            /**
             * Flag indicating whether touch is on upper view.
             */
            boolean mTouchOnOver;

            @Override
            public boolean onDown(MotionEvent e) {
                mIsScrollGesture = false;

                mOverScrollX = 0;
                mTotalVectorX = 0;
                mTotalVectorXInt = 0;
                mTouchOnOver = (e.getX() >= mCurrentCoverLeft);
                mGestureStartLeft = mCurrentCoverLeft;

                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mIsScrollGesture = false;
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!mTouchOnOver) {
                    return false;
                }
                if (mCurrentCoverLeft < mCloseCoverLeft) {
                    return false;
                }

                float vectorX = -distanceX;
                mTotalVectorX += vectorX;
                int vectorXInt = (int) (mTotalVectorX - mTotalVectorXInt);
                mTotalVectorXInt += vectorXInt;

                mIsScrollGesture = true;

                int x = getTargetX((int)Math.abs(mOverScrollX + vectorX), (int)vectorX);
//                Log.e("123: ", ""+x);

                if (x >= mLeftDampDistance) {
                    mOverScrollX += vectorX;
                } else {
                    mOverScrollX = 0;
                }
                immediatelyScrollTo(x);

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                mIsScrollGesture = false;
            }

            private boolean isFlingToRight(float velocityX) {
                return velocityX > 0;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mIsScrollGesture = false;
                if (isLeftOpen()) {
                    if (isFlingToRight(velocityX)) {
                        if (mCurrentCoverLeft < mLeftOpenCoverLeft) {
                            int overX = (mLeftOpenCoverLeft - mCloseCoverLeft) / 8;
                            /*int overDistance;
                            if (0 < velocityX && velocityX < mMinVelocity) {
                                velocityX = mMinVelocity;
                            } else if (-mMinVelocity < velocityX && velocityX < 0) {
                                velocityX = -mMinVelocity;
                            }
                            overDistance = (int) (velocityX / (mMinVelocity * mOverDistance));*/
                            flingTo(mLeftOpenCoverLeft, velocityX, overX);
//                            mNeedRebound = true;
//                            if (mCurrentCoverLeft < mLeftOpenBalanceDistance) {
//                                scrollTo(mLeftOpenCoverLeft/* + mLeftReboundDistance*/,
//                                        FLING_DURATION);
//                            } else {
//                                scrollTo(mLeftOpenCoverLeft/* + mLeftReboundDistance*/,
//                                        SCROLL_DURATION);
//                            }
                        } else {
                            scrollTo(mLeftOpenCoverLeft, SCROLL_DURATION);
                        }
                    } else {
                        flingTo(mCloseCoverLeft, velocityX, 0);
                    }
                }
                return true;
            }
        });
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mMenuView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

//        mMenuView.setBackgroundColor(mContentView.getContext().getResources().getColor(android.R.color.background_dark));

        addView(mMenuView);
        addView(mContentView);
    }

    private int getTargetX(int overX, int dx) {
        int target = mCurrentCoverLeft + dx;
        if (target > mLeftDampDistance && overX > 0) {
            return (int) (mLeftDampDistance + overScrollCurveB(overX));
        } else {
            return target;
        }
    }

    private double f0 = overScrollCurveA(0);

    private double overScrollCurveA(double x) {
        return 2 * (Math.log(1 * (x + 100)) / Math.log(1.1) );
    }

    private double overScrollCurveB(double x) {
        return overScrollCurveA(x) - f0;
    }

    public void superScrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    public void scrollTo(int targetX, int duration) {
        scrollTo(targetX, false, duration);
    }

    private void scrollTo(int targetX, boolean ignoreInterrupt, int duration) {
        if (targetX == mLeftOpenCoverLeft) {
            mAdapter.setOpenedChild(this);
        }

        int startX = mCurrentCoverLeft;
        mScroller.startScroll(startX, 0, targetX - startX, 0, duration, ignoreInterrupt);
        post(mScrollRunner);
    }

    private Runnable mScrollRunner = new Runnable() {
        public void run() {
            boolean notFinished = mScroller.computeScrollOffset();
            immediatelyScrollTo(mScroller.getCurrX());
            if (notFinished) {
                post(this);
            } else {
//                if (mOnClosedListener != null) {
//                    mOnClosedListener.onEditItemClosed();
//                }
            }
        }
    };

    private void flingTo(int targetX, float velocityX, int overX) {
        if (targetX == mLeftOpenCoverLeft) {
            mAdapter.setOpenedChild(this);
        }

        int startX = mCurrentCoverLeft;
        mScroller.fling(startX, 0/*startY*/, (int) velocityX, 0 /*velocityY*/,
                targetX /*minX*/, targetX/*maxX*/, 0/*minY*/, 0/*maxY*/,
                overX/*overX*/, 0/*overY*/);
        post(mScrollRunner);
    }

    /**
     * Scroll to close state or scroll to open state, decided by mLeftOpenBalanceDistance.
     */
    private void scrollIntoSlot() {
        int left = mCurrentCoverLeft;
        int targetX;
        if (mGestureStartLeft == mLeftOpenCoverLeft) {
            targetX = mCloseCoverLeft;
        } else {
            targetX = left > mLeftOpenBalanceDistance ?
                    mLeftOpenCoverLeft : mCloseCoverLeft;
        }
        scrollTo(targetX, SCROLL_DURATION);
    }

    /**
     * Horizontal swipe gesture method.
     */
    private boolean immediatelyScrollTo(int x) {
        int width = mContentView.getWidth();
        if (x < mCloseCoverLeft) {
            x = mCloseCoverLeft;
        }
        mContentView.setLeft(x);
        mContentView.setRight(x + width);
        mCurrentCoverLeft = x;
        return true;
    }

    private void interruptScroller() {
        if (!mScroller.isClearFinished()) {
            mScroller.forceFinished(true);
        }
        removeCallbacks(mScrollRunner);
    }

    private int getCoverCloseLeft() {
        return mCloseCoverLeft;
    }

    public int getCurrentCoverLeft() {
        return mCurrentCoverLeft;
    }

    public boolean isLeftOpen() {
        return mCurrentCoverLeft > mCloseCoverLeft;
    }

    public boolean isClosed() {
        return mCurrentCoverLeft == mCloseCoverLeft && mScroller.isClearFinished();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCloseCoverLeft = mContentView.getLeft();
        if (mCurrentCoverLeft == UNDEFINED) {
            mCurrentCoverLeft = mContentView.getLeft();
        } else {
            immediatelyScrollTo(mCurrentCoverLeft);
        }
        mLeftDampDistance = mMenuView.getLastMenuRightEdge();
//        mLeftOpenBalanceDistance = mLeftDampDistance / 4;
        mLeftOpenBalanceDistance = mMenuView.getMenuViews().get(0).getLeft();
        mLeftOpenCoverLeft = mLeftDampDistance;
    }

    Rect mCoverHitRect;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e("test", "---Item---onInterceptTouchEvent--"+ev.getAction());

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mIgnoreGesture = false;
            if (mAdapter.isAdapterAnimating()) {
                mIgnoreGesture = true;
                return true;
            }
        }

        boolean motionOnCover = isMotionOnCover(ev);
//        Log.e("123: ", "Item motionOnCover---"+motionOnCover);
//        Log.e("123: ", "Item isClearFinished---"+mScroller.isClearFinished());
//        Log.e("123: ", "Item ignoreInterrupt---"+mScroller.ignoreInterrupt());

        if (motionOnCover && !mScroller.isClearFinished()
                && mScroller.ignoreInterrupt()) {
            mIgnoreGesture = true;
            return true;
        } else if (motionOnCover && !isClosed()) {
            return true;
        } else {
            scrollByTouch(ev);
//            Log.e("123: ", "return mIsScrollGesture---"+mIsScrollGesture);
            return mIsScrollGesture;
        }
    }

    /**
     * Check whether the touch event is on upper view.
     */
    private boolean isMotionOnCover(MotionEvent ev) {
        if (mCoverHitRect == null) {
            mCoverHitRect = new Rect();
        }
        mContentView.getHitRect(mCoverHitRect);
        return mCoverHitRect.contains((int) ev.getX(), (int) ev.getY());
    }

    /**
     * Check whether the touch event is on menu.
     */
    private boolean isMotionOnMenu(MotionEvent ev) {
        boolean result = false;
        Rect rect = new Rect();
        for (View menu : mMenuView.getMenuViews()) {
            menu.getHitRect(rect);
            result |= rect.contains((int)ev.getX(), (int)ev.getY());
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e("test", "---Item---onTouchEvent--"+event.getAction());

        if (mIgnoreGesture) {
            // ignore gesture, do nothing.
            return true;
        } else {
            // Close if user touches fully opened cover
            if ((event.getActionMasked() == MotionEvent.ACTION_DOWN)) {
                if(((mCurrentCoverLeft >= mLeftOpenCoverLeft))
                        && (isMotionOnCover(event) || !isMotionOnMenu(event))) {
                    mIgnoreGesture = true;
                    closeAutonomously();
                    return true;
                }
            }
            boolean handled = scrollByTouch(event);
            return super.onTouchEvent(event);
        }
    }

    private boolean scrollByTouch(MotionEvent event) {
        int action = event.getAction();
        // break the scrolling
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                mAdapter.setAdapterAnimating(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                mAdapter.setAdapterAnimating(false);
                break;
            default:
                interruptScroller();
        }

        boolean handled = mGestureDetector.onTouchEvent(event);

        // scroll into snap
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if ((mCurrentCoverLeft != mCloseCoverLeft) && mScroller.isClearFinished()) {
                    // When not scroll over half, auto scroll back.
                    if (mCurrentCoverLeft != mLeftDampDistance) {
                        scrollIntoSlot();
                    }
                }
                if (!isClosed()) {
                    mMenuView.setMenuClickable(true);
                }
        }
        // action down
        handled = handled && ((action & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_DOWN);
        return handled;
    }

    public void setSwipeListAdapter(SwipeListAdapter adapter) {
        mAdapter = adapter;
    }

    public View getContentView() {
        return mContentView;
    }
    public SwipeMenuView getMenuView() {
        return mMenuView;
    }

    /**
     * Item position
     */
    public void setPosition(int position) {
        mPosition = position;
        mMenuView.setPosition(position);
    }
    public int getPosition() {
        return mPosition;
    }

    /**
     * Item close method.
     * Called when one item is open and next Action down MotionEvent is not on menus of that item.
     */
    public void closeAutonomously() {
        scrollTo(mCloseCoverLeft, FLING_DURATION);
        mMenuView.setMenuClickable(false);
        mAdapter.setOpenedChild(null);
    }
}
