package com.sctdroid.pullToRefresh;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sctdroid.pullToRefresh.swipeable.SwipeListView;

import java.util.ArrayList;
import java.util.List;


public abstract class PullToRefreshBaseView extends RelativeLayout {

    private static final int DISTANCE_TO_LOAD_MORE = 100; // PX

    private static final int SHOW_CHECKING_FOR_MAIL_DURATION_IN_MILLIS = 1 * 1000; // 1 seconds
    private static final int SWIPE_TEXT_APPEAR_DURATION_IN_MILLIS = 200;

    // Max number of times we display the same sync turned off warning message in a toast.
    // After we reach this max, and device/account still has sync off, we assume user has
    // intentionally disabled sync and no longer warn.
    private static final int MAX_NUM_OF_SYNC_TOASTS = 5;
    private static final int REFRESH_ANIM_MIN_TIME = 400;
    private static final String LOG_TAG = "PullToRefreshBaseView";
    private static final boolean DBG = false;

    /**
     * Variables for refresh header view.
     */
    private IHeaderBaseView mHeaderBaseView;
    private RefreshHeaderView mHeader;
    private ImageView mHeadIcon;
    private TextView mHeadText;

    private boolean mLoadOnce;

    /**
     * Variables for refresh footer view.
     */
    private IFooterBaseView mFooterBaseView;
    private RefreshFooterView mFooter;
    private ImageView mFootIcon;
    private TextView mFootText;

    /**
     * Status for pull to refresh, including pull up and pull down.
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    public static final int STATUS_REFRESHING = 2;
    public static final int STATUS_REFRESH_FINISHED = 3;

    private int currentStatus = STATUS_REFRESH_FINISHED;
    private int lastStatus = currentStatus;

    public static final int SCROLL_BACK_SPEED = -20;
    public static final int SCROLL_OUT_SPEED = 20;

    /**
     * Refresh icon rotating infinitely animation.
      */
    private Animation mRefreshAnim;

    /**
     * Whether to ignore events in {#dispatchTouchEvent}.
     */
    private boolean mIgnoreTouchEvents = false;

    private boolean mTrackingScrollMovement = false;
    // Y coordinate of where scroll started
    private float mTrackingScrollStartY;
    private float mStartY = 0;
    private float mLastY = 0;
    private float mStartX = 0;
    private float mDeltaY = 0;

    private boolean mDirectUp = false;
    public boolean mIsSyncing = false;
    private boolean isDoingStartRefreshAnim = false;

    private static final int GESTURE_NONE = -1;
    private static final int GESTURE_DOWN = 0;
    private static final int GESTURE_TAP = 1;
    private static final int GESTURE_SCROLL_HORIZONTAL = 2;
    private static final int GESTURE_SCROLL_VERTICAL = 3;
    private static final int GESTURE_LONGPRESS = 4;
    private static final int GESTURE_FLING_HORIZONTAL = 5;
    private static final int GESTURE_FLING_VERTICAL = 6;
    private List<OnStatusChangeListener> mListeners = new ArrayList<OnStatusChangeListener>();
    private long startAnimTime = 0;
    private Handler mHandler = new Handler();
    private final int mScaleTouchSlop;

    /**
     * Handle pull up and pull down gestures.
     */
    private PullDownRefresh mPullDownRefresh = new PullDownRefresh();
    private PullUpLoadMore mPullUpLoadMore = new PullUpLoadMore();
    private PullUpRefresh mPullUpRefresh = new PullUpRefresh();

//    private SwipeDetect mSwipeDetect = new SwipeDetect();

    private RefreshListener mRefreshListener;
    private OnStatusChangeListener mOnStatusChangeListener;
    private ConversationListFooterView.FooterViewClickListener mFooterListener;
    private LayoutInflater mInflater;

    public void addListener(OnStatusChangeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(OnStatusChangeListener listener) {
        mListeners.remove(listener);
    }

    /**
     *  for notify parent currentStatus changes
     */
    public interface OnStatusChangeListener {
        void onStatusChange(int status, int refreshStyle);
    }
    private void notifyAllListeners(int status, int refreshStyle) {
        for (OnStatusChangeListener listener : mListeners) {
            listener.onStatusChange(status, refreshStyle);
        }
    }

    /**
     * Custom attributes.
     * mEnablePullUpRefresh: true for enabled, false for not enabled. Default is false.
     * mPullUpStyle: PULL_UP_STYLE_REFRESH for refresh style, PULL_UP_STYLE_LOAD for load more style. Default is PULL_UP_STYLE_REFRESH.
     */
    private boolean mEnablePullDown;
    private boolean mEnablePullUp;
    private static final int PULL_UP_STYLE_REFRESH = 0;
    private static final int PULL_UP_STYLE_LOAD = 1;
    private int mPullUpStyle;
    private int mBackground;
    private boolean mEnableSwipe;

    /**
     * Check if pull up enabled and get the pull up style.
     */
    private boolean checkPullUpRefresh() {
        return mEnablePullUp && (mPullUpStyle == PULL_UP_STYLE_REFRESH);
    }private boolean checkPullUpLoad() {
        return mEnablePullUp && (mPullUpStyle == PULL_UP_STYLE_LOAD);
    }

    // Instantiated through view inflation
    @SuppressWarnings("unused")
    public PullToRefreshBaseView(Context context) {
        this(context, null);
    }

    public PullToRefreshBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PullToRefreshBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // handle custom attributes.
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PullToRefreshListView, 0, 0);
        try {
            mEnablePullDown = array.getBoolean(R.styleable.PullToRefreshListView_enablePullDown, true);
            mEnablePullUp = array.getBoolean(R.styleable.PullToRefreshListView_enablePullUp, false);
            mPullUpStyle = array.getInteger(R.styleable.PullToRefreshListView_pullUpStyle, PULL_UP_STYLE_REFRESH);
            mBackground = array.getResourceId(R.styleable.PullToRefreshListView_back_ground, 0);
            mEnableSwipe = array.getBoolean(R.styleable.PullToRefreshListView_enableSwipe, false);
        } finally {
            array.recycle();
        }
        if(mBackground!= 0){
            setBackgroundResource(mBackground);
        }
        mRefreshAnim = AnimationUtils.loadAnimation(context, R.anim.refresh_anim);
        mRefreshAnim.setInterpolator(new LinearInterpolator());
        mScaleTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mInflater = LayoutInflater.from(context);
        initViews(attrs);
    }


    protected abstract boolean canPredicateWhenPullDown();
    protected abstract boolean canPredicateWhenPullUpRefresh();
    protected abstract boolean canPredicateWhenPullUpLoad();

    protected abstract boolean isBottom(boolean pullUpRefresh);

    protected abstract boolean isTop();

    protected abstract IHeaderBaseView createRefreshableView(AttributeSet attrs, boolean enableSwipe);

    protected abstract void addMoreView(View footerView);

    protected abstract void removeMoreView(View footerView);

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !mLoadOnce) {
            mLoadOnce = true;
        }
    }

    private void initViews(AttributeSet attrs) {
        if (mEnablePullDown) {
            mInflater.inflate(R.layout.pull_down_to_refresh_base_view, this);
        }
        // for pull up to refresh
        if (checkPullUpRefresh()) {
            mInflater.inflate(R.layout.pull_up_to_refresh_base_view, this);
            mFooter = (RefreshFooterView) findViewById(R.id.pull_to_refresh_foot);
            mFooter.setClipTop(0);
            mFootIcon = (ImageView) mFooter.findViewById(R.id.footer_refresh_icon);
            mFootText = (TextView) mFooter.findViewById(R.id.footer_refresh_text);
        }

        mHeaderBaseView = createRefreshableView(attrs, mEnableSwipe);
        ((View) mHeaderBaseView).setId(mHeaderBaseView.hashCode());
        ((View) mHeaderBaseView).setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        addView((View) mHeaderBaseView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        if (checkPullUpRefresh()) {
            mFooterBaseView = (IFooterBaseView) ((HeaderListView) mHeaderBaseView);
            mFooterBaseView.setRefreshFooterListener(new RefreshFooterView.RefreshFooterListener() {
                @Override
                public void footerClipBottomChanged(int top) {
                    if (mIsFinishing || currentStatus != STATUS_REFRESHING) {
                        return;
                    }
                    mFooter.setClipTop(top);
                }
            });
        }

        // for pull down to refresh
        if (mEnablePullDown) {
            mHeader = (RefreshHeaderView) findViewById(R.id.pull_to_refresh_head);
            mHeader.setClipBottom(mHeaderBaseView.getHeaderHeight());
            mHeadIcon = (ImageView) mHeader.findViewById(R.id.refresh_icon);
            mHeadText = (TextView) mHeader.findViewById(R.id.refresh_text);
            mHeaderBaseView.setRefreshHeaderListener(new RefreshHeaderView.RefreshHeaderListener() {
                @Override
                public void headerClipBottomChanged(int bottom) {
                    if (mIsFinishing || currentStatus != STATUS_REFRESHING) {
                        return;
                    }
                    mHeader.setClipBottom(bottom);
                }
            });
        }

        // for pull up to load more
        if (checkPullUpLoad()) {
            addFooterView();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        log("onAttachedToWindow");
        super.onAttachedToWindow();
        if (mOnStatusChangeListener == null) {
            mOnStatusChangeListener = new OnStatusChangeListener() {
                @Override
                public void onStatusChange(int status, int refreshStyle) {
                    if (status == STATUS_REFRESHING) {
                        if (refreshStyle == PULL_DOWN) {
                            mRefreshListener.onPullDownToRefresh();
                        } else {
                            mRefreshListener.onPullUpTpRefresh();
                        }
                    }
                }
            };
            mListeners.add(mOnStatusChangeListener);
        }
        if (checkPullUpLoad()) {
            if (mFooterListener == null) {
                mFooterListener = new ConversationListFooterView.FooterViewClickListener() {
                    @Override
                    public void onFooterViewLoadMoreClick() {
                        if (mRefreshListener != null) {
                            mRefreshListener.onPullUpToLoad();
                        }
                    }
                };
                mFooterView.setClickListener(mFooterListener);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        log("onDetachedFromWindow");
        super.onDetachedFromWindow();
        if (mOnStatusChangeListener != null) {
            mListeners.remove(mOnStatusChangeListener);
            mOnStatusChangeListener = null;
            if (checkPullUpLoad()) {
                mFooterView.setOnClickListener(null);
            }
        }
        if (checkPullUpLoad()) {
            if (mFooterListener != null) {
                mFooterView.setClickListener(null);
                mFooterListener = null;
            }
        }
    }

    private ConversationListFooterView mFooterView;
    private boolean isFooterAdded = false;

    protected void addFooterView() {
        mFooterView = (ConversationListFooterView) LayoutInflater.from(getContext()).inflate(R.layout.conversation_list_footer_view, null);
//        showLoadMore();
//        mHeaderBaseView.addFooterView(mFooterView);
    }

    private Point mCurrPoint = new Point();
    private Point mDownPoint = new Point();

    private final float SPEED_CARDINAL = 0.012f;
    private float mDownScrollStartY;
    private float mUpScrollStartY;
    private static final float SCROLL_FAC = 0.2f;

    private final int DEFAULT_PRE_POSITION = -2;
    /**
     * Store the previous list item position, avoiding execute same operation many times.
     */
    private int mPrePosition = DEFAULT_PRE_POSITION;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initScrollData();
    }

    // Start to auto-scroll DOWN or UP when touch event is near the edge of the list
    // but not at the edge.
    private void initScrollData() {
        final int paddingTop = getPaddingTop();
        final float listHeight = getHeight() - paddingTop - getPaddingBottom();
        mUpScrollStartY = paddingTop + SCROLL_FAC * listHeight;
        mDownScrollStartY = paddingTop + (1.0f - SCROLL_FAC) * listHeight;
    }


    private void recovery() {
        mPrePosition = DEFAULT_PRE_POSITION;
    }

    private boolean mIfInTouch = false;
    private boolean mStateInReleaseToLoad = false;

    interface Gesture {
        boolean handleTouchEvent(MotionEvent event);
    }

    /**
     * Detect swipe gesture.
     */
//    private boolean swipeFlag;
//    private float mTouchX;
//    private float mTouchY;
//    class SwipeDetect implements Gesture {
//        @Override
//        public boolean handleTouchEvent(MotionEvent ev) {
//            switch (ev.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                    mTouchX = ev.getX();
//                    mTouchY = ev.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    if (swipeFlag) {
//                        return true;
//                    }
//
//                    // whether its swipe gesture
//                    float deltaX = ev.getX() - mTouchX;
//                    float deltaY = ev.getY() - mTouchY;
//
//                    // TODO define swipe gesture
//                    if (deltaX/deltaY > 2) {
//                        swipeFlag = true;
//                        return true;
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    if (swipeFlag) {
//                        swipeFlag = false;
//                        return true;
//                    }
//                    break;
//            }
//            return false;
//        }
//    }

    /**
     * Handle pull down refresh gesture.
     * ACTION_DOWN is related to canPredicateWhenPullDown(), ACTION_MOVE is related to activated.
     */
    class PullDownRefresh implements Gesture {
        private boolean activated = false;

        private boolean predicate(MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                // Only if we have reached the top of the list, any further scrolling
                // can potentially trigger a sync.
                return mEnabledPullDown && canPredicateWhenPullDown();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {}
            return activated;
        }

        @Override
        public boolean handleTouchEvent(MotionEvent event) {
            if (!predicate(event)) {
                return false;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startMovementTracking(event.getY());
                    // Not really actioned the pull down, just needs more motion events to determine
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mHeaderBaseView.getHeaderTop() <= 0) {
                        currentStatus = STATUS_PULL_TO_REFRESH;
                    } else {
                        currentStatus = STATUS_RELEASE_TO_REFRESH;
                    }
                    notifyAllListeners(currentStatus, PULL_DOWN);
                    if (currentStatus == STATUS_PULL_TO_REFRESH
                            || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        updateHeaderPadding(mDeltaY);
                        updateHeaderView();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mTrackingScrollMovement) {
                        cancelMovementTracking();
                    }
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        showRefreshing(true, PULL_DOWN);
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        doFinishRefreshing(PULL_DOWN);
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * Handle pull up refresh gesture.
     * ACTION_DOWN is related to canPredicateWhenPullUpRefresh(), ACTION_MOVE is related to activated.
     */
    class PullUpRefresh implements Gesture {
        private boolean activated = false;

        private boolean predicate(MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                // Only if we have reached the bottom of the list, any further scrolling
                // can potentially trigger a sync.
                return checkPullUpRefresh() && canPredicateWhenPullUpRefresh();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {}
            return activated;
        }

        @Override
        public boolean handleTouchEvent(MotionEvent event) {
            if (!predicate(event)) {
                return false;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startMovementTracking(event.getY());
                    // Not really actioned the pull up, just needs more motion events to determine
                    break;
                case MotionEvent.ACTION_MOVE:
                    // if (((HeaderListView)mFooterBaseView).getScrollY() <= mFooterBaseView.getFooterHeight()) {
                    if (mFooterBaseView.getFooterBottom() <= 0) {
                        currentStatus = STATUS_PULL_TO_REFRESH;
                    } else {
                        currentStatus = STATUS_RELEASE_TO_REFRESH;
                    }
                    notifyAllListeners(currentStatus, PULL_UP);
                    if (currentStatus == STATUS_PULL_TO_REFRESH
                            || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        updateFooterScroll(mDeltaY);
                        updateFooterView();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mTrackingScrollMovement) {
                        cancelMovementTracking();
                    }
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        showRefreshing(true, PULL_UP);
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        doFinishRefreshing(PULL_UP);
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * Handle pull up load more gesture.
     * ACTION_DOWN is related to predicate(), ACTION_MOVE is related to activated.
     */
    class PullUpLoadMore implements Gesture {
        private boolean activated = false;
        private boolean isAnimating = false;
        private boolean predicate(MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                return mFooterView != null && mFooterView.isShowFooter() && !isAnimating
                        && canPredicateWhenPullUpLoad();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            }
            return activated;
        }
        @Override
        public boolean handleTouchEvent(MotionEvent event) {
            if (!predicate(event)) {
                return false;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startMovementTracking(event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Pull up to load more
//                    int hasScrolledY = getScrollY();
//                    if (hasScrolledY > 0) {
//                        scrollBy(0, -adjustScrollY(mDeltaY));
//                    } else if (hasScrolledY == 0 &&  mDeltaY < 0) {
//                        scrollBy(0, -adjustScrollY(mDeltaY));
//                    } else {
//                        scrollTo(0, 0);
//                    }

//                    if (getScrollY() > DISTANCE_TO_LOAD_MORE) {
//                        mStateInReleaseToLoad = true;
//                        mFooterView.setState(ConversationListFooterView.STATE_RELEASE_LOAD_MORE);
//                    } else {
//                        mStateInReleaseToLoad = false;
//                        mFooterView.setState(ConversationListFooterView.STATE_PULL_LOAD_MORE);
//                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mTrackingScrollMovement) {
                        cancelMovementTracking();
                    }
//                    if (mStateInReleaseToLoad) {
//                        mFooterView.performLoading();
//                    }
//                    ValueAnimator refreshBackAnim;
//                    long duration = 300;
//                    refreshBackAnim = ValueAnimator.ofInt(getScrollY(), 0);
//                    // Refresh icon roll back.
//                    refreshBackAnim.setInterpolator(CubicInterpolator.OUT);
//                    refreshBackAnim.setDuration(duration);
//                    refreshBackAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            int y = (Integer) animation.getAnimatedValue();
//                            scrollTo(0, y);
//                        }
//                    });
//                    refreshBackAnim.addListener(new AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animator) {
//                            isAnimating = true;
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            isAnimating = false;
//
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animator) {
//                            isAnimating = false;
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animator) {
//
//                        }
//                    });
//                    refreshBackAnim.start();

                    break;
            }
            return true;
        }
    }

    public void releaseToLoad() {
        mFooterView.setState(ConversationListFooterView.STATE_RELEASE_LOAD_MORE);
        mFooterView.performLoading();
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mTrackingScrollMovement
                && isTop()
                && mEnabledPullDown
                && ev.getY() - mStartY >= mScaleTouchSlop
                && currentStatus != STATUS_REFRESHING) {
            mPullDownRefresh.activated = true;
            mPullUpLoadMore.activated = false;
            mPullUpRefresh.activated = false;
        } else if (mTrackingScrollMovement
                && isBottom(false)
                && mStartY - ev.getY() >= mScaleTouchSlop
                && (!(checkPullUpLoad()) || mFooterView.isEnabled())) {
            mPullDownRefresh.activated = false;
            mPullUpLoadMore.activated = true;
            mPullUpRefresh.activated = false;
        } else if (mTrackingScrollMovement
                && isBottom(true)
                && mStartY - ev.getY() >= mScaleTouchSlop
                && checkPullUpRefresh()
                && currentStatus != STATUS_REFRESHING) {
            mPullDownRefresh.activated = false;
            mPullUpLoadMore.activated = false;
            mPullUpRefresh.activated = true;
        } else {
            mPullDownRefresh.activated = false;
            mPullUpLoadMore.activated = false;
            mPullUpRefresh.activated = false;
        }

        return mPullUpLoadMore.activated | mPullDownRefresh.activated | mPullUpRefresh.activated;
    }

    boolean horizentalSwipeFlag = false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // First check for any events that can trigger end of a swipe, so we can reset
        // mIgnoreTouchEvents back to false (it can only be set to true at beginning of swipe)
        // via {#onBeginSwipe()} callback.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIgnoreTouchEvents = false;
        }
        boolean multiTouch = event.getActionIndex() != 0;
        // Ignore multi-touch if is there is already a gesture (in main
        // pointer)
        // handled by other child
        if (multiTouch) {
            return false;
        }

        if (mIgnoreTouchEvents) {
            return super.dispatchTouchEvent(event);
        }

//        boolean handled = super.dispatchTouchEvent(event);
        // handled = true 1. right swipe 2. list view scrolling
        // handled = false 1. list at top.

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = mStartY = event.getY();
                mStartX = event.getX();
                mDownPoint.set((int) event.getX(), (int) event.getY());
                mIfInTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY(0);
                mDeltaY = y - mLastY;
                mLastY = y;
                mDirectUp = mDeltaY < 0;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIfInTouch = false;
                recovery();
                break;
        }
        boolean superRet = super.dispatchTouchEvent(event);
//        if (mSwipeDetect.handleTouchEvent(event)) {
//            return superRet;
//        }
        // if any item is swiping or open, do not check pull refresh.
        if (checkSwipeGesture() || horizentalSwipeFlag) {
            if (!horizentalSwipeFlag) {
                horizentalSwipeFlag = true;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    horizentalSwipeFlag = false;
                    break;
            }
            return true;
        }

        boolean ret = mPullDownRefresh.handleTouchEvent(event)
                | (checkPullUpLoad() && mPullUpLoadMore.handleTouchEvent(event))
                | (checkPullUpRefresh() && mPullUpRefresh.handleTouchEvent(event));
        return ret | superRet;
    }

    /**
     * Check whether swipe gesture is detected.
     * @return boolean
     */
    private boolean checkSwipeGesture() {
        return mHeaderBaseView instanceof SwipeListView &&
                ((SwipeListView) mHeaderBaseView).ifSwipingOrOpen();
    }
//    @Override
//    public void draw(Canvas canvas) {
//        if (mHeader.getVisibility() == View.VISIBLE) {
//            View firstItem = mHeaderBaseView.getChildAt(0);
//            int firstItemTop = firstItem != null ? firstItem.getTop() : 0;
//            mHeader.setClipBottom(firstItemTop + mHeaderBaseView.getHeaderHeight());
//        }
//        super.draw(canvas);
//    }

    /**
     * adjust header padding in list view, play icon animation, set text alpha value.
     * @param scrollY: raw padding value, to be adjusted.
     */
    private void updateHeaderPadding(float scrollY) {
        mHeaderBaseView.setHeaderPadding((int) (mHeaderBaseView.getHeaderTop() + adjustScrollY(scrollY)));

        if (!mIsSyncing) {
            mHeader.setTranslationY(mHeaderBaseView.getHeaderTop() / 2);
            mHeadText.setAlpha(getRefreshTextAlpha());
            setHeadIcon(false);
        }
    }
    /**
     * adjust footer padding in list view, play icon animation, set text alpha value.
     * @param scrollY: raw padding value, to be adjusted.
     */
    private void updateFooterScroll(float scrollY) {
        mFooterBaseView.setFooterPadding((int) (mFooterBaseView.getFooterBottom() + adjustScrollYPullUp(scrollY)));
        mFooterBaseView.scrollToBottom();

        if (!mIsSyncing) {
            mFooter.setTranslationY(-mFooterBaseView.getFooterBottom() / 2);
            mFootText.setAlpha(getRefreshTextAlphaPullUp());
            setHeadIcon(true);
        }
    }

    private float getRefreshTextAlpha() {
        float headerPadding = mHeader.getTranslationY() * 2;
        if (headerPadding < -190) {
            return 0;
        }
        if (headerPadding < 0) {
            return Math.abs((190 - Math.abs(headerPadding)) / 190);
        } else {
            return 1;
        }
    }

    /**
     * Pull up refresh version of getRefreshTextAlpha().
     * @return
     */
    private float getRefreshTextAlphaPullUp() {
        float footerScroll = mFooter.getTranslationY() * 2;
        if (footerScroll > 190) {
            return 0;
        }
        if (footerScroll > 0) {
            return Math.abs((190 - Math.abs(footerScroll)) / 190);
        } else {
            return 1;
        }
    }

    private int adjustScrollY(float y) {
        float result = 0;

        int hasScrolled;
        if (mDirectUp) {
            hasScrolled = getScrollY();
        } else {
            hasScrolled = mHeaderBaseView.getHeaderTop();
        }
        if (hasScrolled < 25) {
            result = y;
        } else if (hasScrolled < 70) {
            result = y / 1.5f;
        } else if (hasScrolled < 100) {
            result = y / 2f;
        } else if (hasScrolled < 150) {
            result = y / 2.5f;
        } else {
            result = y / 3f;
        }
        return (int) result;
    }

    /**
     * adjustScrollY(float y) for pull up refresh version.
     * @param y: raw scrollY
     * @return scrollY after adjust.
     */
    private int adjustScrollYPullUp(float y) {
        float result = 0;

        int hasScrolled;
        if (!mDirectUp) {
            hasScrolled = getScrollY();
        } else {
            hasScrolled = mFooterBaseView.getFooterBottom();
        }
        if (hasScrolled < 25) {
            result = y;
        } else if (hasScrolled < 70) {
            result = y / 1.5f;
        } else if (hasScrolled < 100) {
            result = y / 2f;
        } else if (hasScrolled < 150) {
            result = y / 2.5f;
        } else {
            result = y / 3f;
        }
        return (int) -result;
    }

    private int mLastNumber = 1;
    private boolean mHasClearRotation = true;
    private static final int ICON_ANIM_DIV = 45;
    private static final int ICON_ANIM_LAST = 85;
    private static final int ICON_ANIM_FIRST = 1;
    private static final float ICON_ANIM_RATE = 5.3f;

    /*
     * To do pull-refresh icon animation. 45 is divide for pull-in and back-out. Use rate to control rate of this animation.
     */
    private void setHeadIcon(boolean footer) {
        float translationY;
        if (footer) {
            translationY = mFooterBaseView.getFooterBottom() + mFooterBaseView.getFooterHeight();
        } else {
            translationY = mHeaderBaseView.getHeaderTop() + mHeaderBaseView.getHeaderHeight();
        }

        int number = (int) (translationY / ICON_ANIM_RATE);
        if (number <= 0) {
            number = 1;
        }
        isDoingStartRefreshAnim = true;
        if (number < ICON_ANIM_DIV) {
            if (!mHasClearRotation) {
                if (footer) {
                    mFootIcon.setRotation(0);
                } else {
                    mHeadIcon.setRotation(0);
                }
                mHasClearRotation = true;
            }
            try {
                if (number != mLastNumber) {
                    if (footer) {
                        updateFootIcon(number);
                    } else {
                        updateHeadIcon(number);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mHasClearRotation) {
                if (footer) {
                    updateFootIcon(ICON_ANIM_DIV);
                } else {
                    updateHeadIcon(ICON_ANIM_DIV);
                }
                mHasClearRotation = false;
            }
            if (mLastNumber != ICON_ANIM_DIV) {
                if (footer) {
                    updateFootIcon(ICON_ANIM_DIV);
                } else {
                    updateHeadIcon(ICON_ANIM_DIV);
                }
            }
            if (number != mLastNumber) {
                if (footer) {
                    mFootIcon.setRotation((number - ICON_ANIM_DIV) * 5);
                } else {
                    mHeadIcon.setRotation((number - ICON_ANIM_DIV) * 5);
                }
                mLastNumber = number;
            }
        }
    }

    /**
     * For pull to refresh
     * @param byPull: if started by pull.
     */
    public static final int PULL_UP = 0;
    public static final int PULL_DOWN = 1;
    public void showRefreshing(boolean byPull, final int refreshMethod) {
        log("showRefreshing byPull " + byPull + " mLastNumber " + mLastNumber);
        startAnimTime = AnimationUtils.currentAnimationTimeMillis();
        mTrackingScrollMovement = false;
        currentStatus = STATUS_REFRESHING;

        if (refreshMethod == PULL_DOWN) {
            updateHeaderView();
        }
        else {
            updateFooterView();
        }

        notifyAllListeners(currentStatus, refreshMethod);

        if (refreshMethod == PULL_DOWN) {
            mHeadIcon.clearAnimation();
        }
        else {
            mFootIcon.clearAnimation();
        }

        if (!byPull) {
            mHeaderBaseView.scrollToTop();
            mLastNumber = 1;
            mHeadIcon.setRotation(0);
            updateHeadIcon(1);
            ValueAnimator refreshToAnim = ValueAnimator.ofInt(1, ICON_ANIM_DIV);
            refreshToAnim.setInterpolator(CubicInterpolator.OUT);
            refreshToAnim.setDuration(300);
            refreshToAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (Integer) animation.getAnimatedValue();
                    updateHeadIcon(y);
                }
            });
            refreshToAnim.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mRefreshAnim != null) {
                        mHeadIcon.startAnimation(mRefreshAnim);
                        startAnimTime = AnimationUtils.currentAnimationTimeMillis();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

            });

            refreshToAnim.start();
            mHeadText.animate().alpha(1).setDuration(300).setInterpolator(CubicInterpolator.OUT)
                    .start();
        } else {
            if (refreshMethod == PULL_DOWN) {
                updateHeadIcon(ICON_ANIM_DIV);
            } else {
                updateFootIcon(ICON_ANIM_DIV);
            }

            if (mRefreshAnim != null) {
                if (refreshMethod == PULL_DOWN) {
                    mHeadIcon.startAnimation(mRefreshAnim);
                } else {
                    mFootIcon.startAnimation(mRefreshAnim);
                }
            }
        }
        if (refreshMethod == PULL_DOWN) {
            int topPadding = (int) mHeader.getTranslationY();
            ValueAnimator mHeadAnim = ValueAnimator.ofInt(topPadding, 0);
            mHeadAnim.setInterpolator(CubicInterpolator.OUT);
            mHeadAnim.setDuration(200);
            mHeadAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (Integer) animation.getAnimatedValue();
                    mHeader.setTranslationY(y);
                    mHeaderBaseView.setHeaderPadding(y * 2);
                }
            });
            mHeadAnim.start();
        } else {
            int bottomPadding = (int) mFooter.getTranslationY();
            ValueAnimator mFootAnim = ValueAnimator.ofInt(bottomPadding, 0);
            mFootAnim.setInterpolator(CubicInterpolator.OUT);
            mFootAnim.setDuration(200);
            mFootAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (Integer) animation.getAnimatedValue();
                    mFooter.setTranslationY(y);
                    // ((HeaderListView)mFooterBaseView).scrollTo(0, (-y * 2) + mFooterBaseView.getFooterHeight());
                    mFooterBaseView.setFooterPadding(-y * 2);
                }
            });
            mFootAnim.start();
        }
        mIsSyncing = true;
    }

    public void finishRefreshing(final int refreshMethod) {
        long curTime = AnimationUtils.currentAnimationTimeMillis();
        if (curTime - startAnimTime >= REFRESH_ANIM_MIN_TIME) {
            doFinishRefreshing(refreshMethod);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doFinishRefreshing(refreshMethod);
                }
            }, REFRESH_ANIM_MIN_TIME - (curTime - startAnimTime));
        }
    }

    private boolean mIsFinishing = false;

    private void doFinishRefreshing(final int refreshMethod) {
        if (mIsFinishing) {
            return;
        }
        isDoingStartRefreshAnim = false;
        mIsFinishing = true;

        if (refreshMethod == PULL_DOWN) {
            mHeadIcon.clearAnimation();
        } else {
            mFootIcon.clearAnimation();
        }
        // if (!mIfInTouch && mHeaderBaseView.getScrollY() == 0) {
        // log("setSelection 0");
        // mHeaderBaseView.setSelection(0);
        // }
        ValueAnimator refreshBackAnim;
        long duration = 0;
        // If mLastNumber < ICON_ANIM_DIV means it's not a complete refresh.
        if (mLastNumber < ICON_ANIM_DIV) {
            refreshBackAnim = ValueAnimator.ofInt(mLastNumber, ICON_ANIM_FIRST);
            duration = mLastNumber * 4;
        } else {
            if (refreshMethod == PULL_DOWN) {
                mHeadIcon.setRotation(0);
            } else {
                mFootIcon.setRotation(0);
            }

            refreshBackAnim = ValueAnimator.ofInt(ICON_ANIM_DIV, ICON_ANIM_LAST);
            duration = 500;
        }
        // Refresh icon roll back.
        refreshBackAnim.setInterpolator(CubicInterpolator.OUT);
        refreshBackAnim.setDuration(duration);
        refreshBackAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isDoingStartRefreshAnim) {
                    int y = (Integer) animation.getAnimatedValue();
                    if (refreshMethod == PULL_DOWN) {
                        updateHeadIcon(y);
                    } else {
                        updateFootIcon(y);
                    }
                }
            }
        });
        refreshBackAnim.start();
        if (refreshMethod == PULL_DOWN) {
            // Refresh text alpha out.
            mHeadText.animate().setDuration(300).setInterpolator(CubicInterpolator.OUT)
                    .alpha(0f).start();
            // Refresh icon alpha out.
            mHeadIcon.animate().setDuration(300).setInterpolator(CubicInterpolator.OUT)
                    .alpha(0f).start();
            // Refresh head  & list view translate out.
            ValueAnimator mHeadAnim = ValueAnimator.ofInt(mHeaderBaseView.getHeaderTop(),
                    -1 * mHeaderBaseView.getHeaderHeight());
            mHeadAnim.setInterpolator(CubicInterpolator.OUT);
            mHeadAnim.setDuration(300);
            final int headerClipBottom = mHeader.getClipBottom();
            mHeadAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (Integer) animation.getAnimatedValue();
                    // ListView move from 0 to -240
                    mHeaderBaseView.setHeaderPadding(y);
                    mHeader.setClipBottom(y + headerClipBottom);
//                Refresh Header translate from 0 to -120
                    if (!mIfInTouch) {
                        mHeader.setTranslationY(y / 2);
                    }
                }
            });
            mHeadAnim.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsSyncing = false;
                    mIsFinishing = false;
                    currentStatus = STATUS_REFRESH_FINISHED;
                    mHeadIcon.setAlpha(0f);
                    mHeader.setClipBottom(mHeaderBaseView.getHeaderHeight());
                    updateHeaderView();
                    notifyAllListeners(currentStatus, refreshMethod);
                    updateHeadIcon(1);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            mHeadAnim.setStartDelay(50);
            mHeadAnim.start();
            if (checkPullUpLoad()) {
                if (mFooterView.isShowFooter()) {
                    mFooterView.setState(ConversationListFooterView.STATE_PULL_LOAD_MORE);
                }
            }
        } else if (refreshMethod == PULL_UP) {
            // Refresh text alpha out.
            mFootText.animate().setDuration(300).setInterpolator(CubicInterpolator.OUT)
                    .alpha(0f).start();
            // Refresh icon alpha out.
            mFootIcon.animate().setDuration(300).setInterpolator(CubicInterpolator.OUT)
                    .alpha(0f).start();
            // Refresh head  & list view translate out.
            ValueAnimator mFootAnim = ValueAnimator.ofInt(mFooterBaseView.getFooterBottom(), -mFooterBaseView.getFooterHeight());
            mFootAnim.setInterpolator(CubicInterpolator.OUT);
            mFootAnim.setDuration(300);
            final int footerClipTop = mFooter.getClipTop();
            mFootAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (Integer) animation.getAnimatedValue();
                    // ListView move from 0 to -240
                    mFooterBaseView.setFooterPadding(y);

                    mFooter.setClipTop(-y + footerClipTop);
                    // Refresh Header translate from 0 to 120
                    if (!mIfInTouch) {
                        mFooter.setTranslationY(-y / 2);
                    }
                }
            });
            mFootAnim.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsSyncing = false;
                    mIsFinishing = false;
                    currentStatus = STATUS_REFRESH_FINISHED;
                    mFootIcon.setAlpha(0f);
                    mFooter.setClipTop(0);

                    // mHeader.getClipBottom() return 0 after footer pull up refreshing.
                    mHeader.setClipBottom(mHeaderBaseView.getHeaderHeight());

                    updateFooterView();
                    notifyAllListeners(currentStatus, refreshMethod);
                    updateFootIcon(1);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            mFootAnim.setStartDelay(50);
            mFootAnim.start();
        }
    }

    /*public void onResume() {
        if (currentStatus != STATUS_REFRESHING) {
            mIsSyncing = false;
            mIsFinishing = false;
            currentStatus = STATUS_REFRESH_FINISHED;
            notifyAllListeners(currentStatus);
        }
    }*/

    private void updateHeadIcon(int position) {
        mLastNumber = position;
        mHeadIcon.getBackground().setLevel(position + 1);
    }
    private void updateFootIcon(int position) {
        mLastNumber = position;
        mFootIcon.getBackground().setLevel(position + 1);
    }

    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                mHeadText.setText(getResources().getString(R.string.pull_down_to_refresh));
                mHeadIcon.setAlpha(1f);
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                mHeadText.setText(getResources().getString(R.string.pull_down_to_release));
                mHeadIcon.setAlpha(1f);
            } else if (currentStatus == STATUS_REFRESHING) {
                mHeadText.setText(getResources().getString(R.string.pull_down_refreshing));
                mHeadIcon.setAlpha(1f);
            } else if (currentStatus == STATUS_REFRESH_FINISHED) {
                mHeadText.setText(getResources().getString(R.string.pull_down_to_refresh));
                mHeadIcon.setAlpha(0f);
            }
        }
        lastStatus = currentStatus;
    }

    private void updateFooterView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                mFootText.setText(getResources().getString(R.string.pull_up_to_refresh));
                mFootIcon.setAlpha(1f);
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                mFootText.setText(getResources().getString(R.string.pull_up_to_release));
                mFootIcon.setAlpha(1f);
            } else if (currentStatus == STATUS_REFRESHING) {
                mFootText.setText(getResources().getString(R.string.pull_up_refreshing));
                mFootIcon.setAlpha(1f);
            } else if (currentStatus == STATUS_REFRESH_FINISHED) {
                mFootText.setText(getResources().getString(R.string.pull_up_to_refresh));
                mFootIcon.setAlpha(0f);
            }
        }
        lastStatus = currentStatus;
    }

    // end=======

    private void startMovementTracking(float y) {
//        LogUtils.d(LOG_TAG, "Start swipe to refresh tracking");
        mTrackingScrollMovement = true;
        mTrackingScrollStartY = y;
    }

    private void cancelMovementTracking() {
        if (mTrackingScrollMovement) {
            mTrackingScrollMovement = false;
        }
    }

    private void log(String log) {
        if (DBG)
            Log.d(LOG_TAG, log);
    }

    public IHeaderBaseView getRefreshableView() {
        return mHeaderBaseView;
    }

    public interface RefreshListener {
        public void onPullDownToRefresh();
        public void onPullUpTpRefresh();

        public void onPullUpToLoad();
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public void showLoadMore() {
        if (checkPullUpLoad()) {
            this.mFooterView.setState(ConversationListFooterView.STATE_PULL_LOAD_MORE);
            if (!isFooterAdded) {
                addMoreView(mFooterView);
                isFooterAdded = true;
            }
        }
    }

    public void hideLoadMore() {
        if (checkPullUpLoad()) {
            this.mFooterView.setState(ConversationListFooterView.STATE_GONE);
            if (isFooterAdded) {
                removeMoreView(mFooterView);
                isFooterAdded = false;
            }
        }
    }

    /**
     * Disable pull up to refresh.
     * When there is no more old articles, disable pull up refresh function.
     */
    public void disablePullUp() {
        mEnablePullUp = false;
    }

    /**
     * Enable pull up to refresh.
     * When there is more old articles, disable pull up refresh function.
     */
    public void enablePullUp(){
        mEnablePullUp = true;
    }

    /**
     * Used for pull up refresh.
     * When pull up refreshing success, do not show roll back animation.
     * Disappear the footer and replace it by old article, make the user know that he/she can scroll down to see more articles.
     */
    public void disappearFooter() {
        long curTime = AnimationUtils.currentAnimationTimeMillis();
        if (curTime - startAnimTime >= REFRESH_ANIM_MIN_TIME) {
            doDisappearFooter();
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doDisappearFooter();
                }
            }, REFRESH_ANIM_MIN_TIME - (curTime - startAnimTime));
        }
    }

    /**
     * Disappearing the footer operations are actually here.
     */
    private void doDisappearFooter() {
        if (mIsFinishing) {
            return;
        }
        isDoingStartRefreshAnim = false;
        mIsFinishing = true;

        mFootText.setAlpha(0);
        mFootIcon.setAlpha(0.0f);
        mFootIcon.clearAnimation();
        ((HeaderListView) mFooterBaseView).removeFooter();
        ((HeaderListView) mFooterBaseView).addFooter();

        mIsSyncing = false;
        mIsFinishing = false;
        currentStatus = STATUS_REFRESH_FINISHED;

        // mHeader.getClipBottom return 0 after footer pull up refreshing.
        mHeader.setClipBottom(mHeaderBaseView.getHeaderHeight());

        updateFooterView();
        notifyAllListeners(currentStatus, PULL_UP);
        updateFootIcon(1);
    }

    private boolean mEnabledPullDown = true;
    public void setEnabledPullDownToRefresh(boolean enabled) {
        if (mEnabledPullDown != enabled){
            mEnabledPullDown = enabled;
        }
    }
}

